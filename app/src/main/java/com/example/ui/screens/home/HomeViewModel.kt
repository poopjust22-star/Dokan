package com.example.ui.screens.home

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Product
import com.example.data.local.entity.SaleOrder
import com.example.data.repository.HardwareRepository
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.toBengaliNumerals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HomeSummaryMetrics(
    val totalSales: Double = 0.0,
    val totalProfit: Double = 0.0,
    val ordersCount: Int = 0,
    val itemsSoldCount: Int = 0
)

data class PeriodFigures(
    val sales: Double = 0.0,
    val purchases: Double = 0.0,
    val expenses: Double = 0.0
)

data class CashflowDay(
    val dayName: String,
    val moneyIn: Double,
    val moneyOut: Double
)

data class CashflowChartState(
    val days: List<CashflowDay> = emptyList(),
    val totalIn: Double = 0.0,
    val totalOut: Double = 0.0,
    val netCashflow: Double = 0.0
)

data class HomeShortcutItem(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val iconName: String,
    val isEnabled: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = HardwareRepository(
        database.productDao(),
        database.saleOrderDao(),
        database.categoryDao(),
        database.purchaseDao(),
        database.customerDao(),
        database.supplierDao(),
        database.supplierPaymentDao(),
        database.customerPaymentDao(),
        database.expenseDao()
    )

    private val prefs = application.getSharedPreferences("home_dashboard_prefs", Context.MODE_PRIVATE)

    private val startOfDay: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayMetrics: StateFlow<HomeSummaryMetrics> = repository.allSales
        .map { sales ->
            val todaySales = sales.filter { it.timestamp >= startOfDay }
            val totalAmount = todaySales.sumOf { it.totalAmount }
            val totalProfit = todaySales.sumOf { (it.totalAmount * 0.20) } // Standard estimate or actual profit
            val orders = todaySales.size
            val itemsSold = todaySales.sumOf { it.totalItemsCount }
            HomeSummaryMetrics(
                totalSales = totalAmount,
                totalProfit = totalProfit,
                ordersCount = orders,
                itemsSoldCount = itemsSold
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeSummaryMetrics())

    val customerBaki: StateFlow<Double> = repository.totalUnpaidBaki
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val supplierDue: StateFlow<Double> = repository.totalAmountOwedToSuppliers
        .map { it ?: 5800.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5800.0)

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSales: StateFlow<List<SaleOrder>> = repository.recentSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Period Figures (Sales, Purchase, Expense)
    val periodFigures: StateFlow<PeriodFigures> = combine(
        repository.allSales,
        repository.allPurchases,
        repository.allExpenses
    ) { sales, purchases, expenses ->
        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        val monthSales = sales.filter { it.timestamp >= monthStart }.sumOf { it.totalAmount }
        val monthPurchases = purchases.filter { it.timestamp >= monthStart }.sumOf { it.totalCost }
        val monthExpenses = expenses.filter { it.timestamp >= monthStart }.sumOf { it.amount }

        PeriodFigures(
            sales = monthSales,
            purchases = monthPurchases,
            expenses = monthExpenses
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PeriodFigures())

    // Total Balance Card: Cash in Hand & Bank
    val totalBalance: StateFlow<Double> = combine(
        repository.allSales,
        repository.allCustomerPayments,
        repository.allSupplierPayments,
        repository.allExpenses
    ) { sales, custPayments, supPayments, expenses ->
        val cashSales = sales.sumOf { it.paidAmount }
        val collections = custPayments.sumOf { it.amount }
        val outSupplier = supPayments.sumOf { it.amount }
        val outExpenses = expenses.sumOf { it.amount }
        val baseOpeningBalance = 45000.0
        val balance = baseOpeningBalance + cashSales + collections - outSupplier - outExpenses
        balance.coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 45000.0)

    // Cashflow Chart: 7 Days (Money In vs Money Out)
    val cashflow7Days: StateFlow<CashflowChartState> = combine(
        repository.allSales,
        repository.allCustomerPayments,
        repository.allPurchases,
        repository.allExpenses,
        repository.allSupplierPayments
    ) { sales, custPayments, purchases, expenses, supPayments ->
        val list = mutableListOf<CashflowDay>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.US)

        for (i in 6 downTo 0) {
            val targetCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val dayStart = targetCal.timeInMillis
            targetCal.set(Calendar.HOUR_OF_DAY, 23)
            targetCal.set(Calendar.MINUTE, 59)
            targetCal.set(Calendar.SECOND, 59)
            val dayEnd = targetCal.timeInMillis

            val inSales = sales.filter { it.timestamp in dayStart..dayEnd }.sumOf { it.paidAmount }
            val inPayments = custPayments.filter { it.timestamp in dayStart..dayEnd }.sumOf { it.amount }
            val moneyIn = inSales + inPayments

            val outPurchases = purchases.filter { it.timestamp in dayStart..dayEnd }.sumOf { it.paidAmount }
            val outExpenses = expenses.filter { it.timestamp in dayStart..dayEnd }.sumOf { it.amount }
            val outSupPayments = supPayments.filter { it.timestamp in dayStart..dayEnd }.sumOf { it.amount }
            val moneyOut = outPurchases + outExpenses + outSupPayments

            val label = dayFormat.format(Date(dayStart))
            list.add(CashflowDay(dayName = label, moneyIn = moneyIn, moneyOut = moneyOut))
        }

        val totalIn = list.sumOf { it.moneyIn }
        val totalOut = list.sumOf { it.moneyOut }
        CashflowChartState(
            days = list,
            totalIn = totalIn,
            totalOut = totalOut,
            netCashflow = totalIn - totalOut
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CashflowChartState())

    // Customizable Shortcuts
    private val DEFAULT_SHORTCUTS = listOf(
        HomeShortcutItem("add_party", "Add Party", "পার্টি যোগ", "person_add", true),
        HomeShortcutItem("sales_invoice", "Sales Invoice", "নতুন চালান", "receipt", true),
        HomeShortcutItem("payment_in", "Payment In", "টাকা জমা", "payment_in", true),
        HomeShortcutItem("payment_out", "Payment Out", "টাকা প্রদান", "payment_out", true),
        HomeShortcutItem("purchase", "Purchase", "ক্রয় / খরিদ", "shopping_cart", true),
        HomeShortcutItem("add_item", "Add Item", "পণ্য যোগ", "add_box", true),
        HomeShortcutItem("expense", "Expense", "দোকান খরচ", "money_off", true),
        HomeShortcutItem("add_note", "Add Note", "হিসাব নোট", "note_add", true),
        HomeShortcutItem("other_income", "Other Income", "অন্যান্য আয়", "account_balance", false),
        HomeShortcutItem("sales_return", "Sales Return", "বিক্রি ফেরত", "assignment_return", false),
        HomeShortcutItem("purchase_return", "Purchase Return", "ক্রয় ফেরত", "reply", false),
        HomeShortcutItem("quotation", "Quotation", "দরপত্র", "description", false)
    )

    private val _shortcuts = MutableStateFlow(loadShortcuts())
    val shortcuts: StateFlow<List<HomeShortcutItem>> = _shortcuts.asStateFlow()

    private fun loadShortcuts(): List<HomeShortcutItem> {
        val enabledSet = prefs.getStringSet("enabled_shortcuts", null)
        return if (enabledSet == null) {
            DEFAULT_SHORTCUTS
        } else {
            DEFAULT_SHORTCUTS.map { it.copy(isEnabled = enabledSet.contains(it.id)) }
        }
    }

    fun toggleShortcut(id: String) {
        val updated = _shortcuts.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
        _shortcuts.value = updated
        val set = updated.filter { it.isEnabled }.map { it.id }.toSet()
        prefs.edit().putStringSet("enabled_shortcuts", set).apply()
    }

    // Promotional Banner Area (Tips/Videos - User Editable)
    var bannerTitle by mutableStateOf(
        prefs.getString("banner_title", "দোকানের হিসাব দ্রুত রাখার সেরা টিপস") ?: "দোকানের হিসাব দ্রুত রাখার সেরা টিপস"
    )
        private set

    var bannerSubtitle by mutableStateOf(
        prefs.getString(
            "banner_subtitle",
            "বাকি আদায়ে গ্রাহককে নিয়মিত এসএমএস পাঠান এবং প্রতিদিন ক্যাশ ড্রয়ার হিসাব মিলিয়ে দোকান বন্ধ করুন।"
        ) ?: "বাকি আদায়ে গ্রাহককে নিয়মিত এসএমএস পাঠান এবং প্রতিদিন ক্যাশ ড্রয়ার হিসাব মিলিয়ে দোকান বন্ধ করুন।"
    )
        private set

    fun updateBanner(title: String, subtitle: String) {
        bannerTitle = title.trim()
        bannerSubtitle = subtitle.trim()
        prefs.edit()
            .putString("banner_title", bannerTitle)
            .putString("banner_subtitle", bannerSubtitle)
            .apply()
    }

    fun getFormattedDate(language: AppLanguage): String {
        val now = Date()
        return if (language == AppLanguage.BANGLA) {
            val dayName = SimpleDateFormat("EEEE", Locale("bn", "BD")).format(now)
            val dayOfMonth = SimpleDateFormat("dd", Locale.US).format(now)
            val monthName = SimpleDateFormat("MMMM", Locale("bn", "BD")).format(now)
            val year = SimpleDateFormat("yyyy", Locale.US).format(now)
            "$dayName, ${toBengaliNumerals(dayOfMonth)} $monthName ${toBengaliNumerals(year)}"
        } else {
            SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US).format(now)
        }
    }
}
