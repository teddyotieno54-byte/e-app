package com.example.data.database

import com.example.data.dao.BizlyDao
import com.example.data.model.Business
import com.example.data.model.Customer
import com.example.data.model.Debt
import com.example.data.model.DebtPayment
import com.example.data.model.Employee
import com.example.data.model.Expense
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.NotificationItem
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StockAdjustment

object DemoDataSeeder {

    suspend fun seedDemoData(dao: BizlyDao) {
        // Clear existing tables
        dao.clearProducts()
        dao.clearCustomers()
        dao.clearSales()
        dao.clearSaleItems()
        dao.clearExpenses()
        dao.clearDebts()
        dao.clearDebtPayments()
        dao.clearInvoices()
        dao.clearInvoiceItems()
        dao.clearEmployees()
        dao.clearNotifications()
        dao.clearStockAdjustments()

        val now = System.currentTimeMillis()
        val dayMillis = 86400000L

        // 1. Business Workspace: TechPoint Electronics
        val business = Business(
            id = 1,
            name = "TechPoint Electronics",
            category = "Electronics & Gadgets",
            currency = "KES",
            location = "Moi Avenue, Digital Mall Shop G14, Nairobi",
            phone = "+254 712 345 678",
            email = "sales@techpoint-ke.com",
            taxRate = 16.0,
            invoicePrefix = "TPE",
            mpesaTillOrPaybill = "782910",
            mpesaType = "Till (Buy Goods)",
            subscriptionPlan = "Business",
            createdAt = now - 90 * dayMillis
        )
        dao.insertBusiness(business)

        // 2. 30 Realistic Products
        val products = listOf(
            Product(1, 1, "Samsung Galaxy A15 128GB", "SAM-A15-BLU", "Smartphones", 16500.0, 21500.0, 14, 5, "Samsung Kenya", "6.5 Super AMOLED, 50MP Camera", "In Stock"),
            Product(2, 1, "Redmi Note 13 256GB", "XIA-RN13-BLK", "Smartphones", 22000.0, 27999.0, 8, 5, "Xiaomi East Africa", "108MP Camera, 67W Turbo Charge", "In Stock"),
            Product(3, 1, "iPhone 13 128GB Midnight (Grade A)", "APL-IP13-MDT", "Smartphones", 58000.0, 69500.0, 3, 4, "US Direct Refurb", "Battery Health 92%+, Factory Unlocked", "Low Stock"),
            Product(4, 1, "Oraimo 20000mAh Powerbank Toast 10", "ORM-PB20-TOAST", "Accessories", 2100.0, 3200.0, 25, 6, "Oraimo Kenya Official", "Dual USB + Type-C Fast Charging", "In Stock"),
            Product(5, 1, "Anker 65W GaNPrime Fast Charger", "ANK-GAN-65W", "Chargers & Cables", 3800.0, 5500.0, 12, 4, "Anker Direct", "3 Ports (2 USB-C + 1 USB-A)", "In Stock"),
            Product(6, 1, "Apple 20W USB-C Power Adapter", "APL-ADP-20W", "Chargers & Cables", 2200.0, 3500.0, 2, 5, "Official Apple Dist.", "Original Apple Fast Adapter", "Low Stock"),
            Product(7, 1, "Type-C to Type-C Braided Cable 1.5m", "CAB-CC-15M", "Chargers & Cables", 350.0, 800.0, 45, 10, "Baseus Depot", "100W Power Delivery", "In Stock"),
            Product(8, 1, "Lightning to Type-C Fast Cable 1.2m", "CAB-LT-12M", "Chargers & Cables", 400.0, 950.0, 30, 10, "Baseus Depot", "MFi Certified Braided Nylon", "In Stock"),
            Product(9, 1, "JBL Flip 6 Bluetooth Speaker (Teal)", "JBL-FLP6-TEA", "Audio", 11500.0, 15500.0, 6, 3, "Harman East Africa", "IP67 Waterproof & Dustproof", "In Stock"),
            Product(10, 1, "Sony WH-1000XM4 Wireless Headphones", "SNY-WH4-SIL", "Audio", 28000.0, 36000.0, 2, 3, "Sony Regional Hub", "Industry Leading Active Noise Canceling", "Low Stock"),
            Product(11, 1, "Oraimo FreePods 4 ANC Earbuds", "ORM-FP4-WHT", "Audio", 2800.0, 4200.0, 19, 5, "Oraimo Kenya Official", "Active Noise Cancellation + App Support", "In Stock"),
            Product(12, 1, "HP 250 G9 Core i5 8GB 512GB SSD", "HP-250G9-I5", "Computers", 52000.0, 64500.0, 5, 2, "Mitsumi Distribution", "Intel Core i5 12th Gen, 15.6 FHD", "In Stock"),
            Product(13, 1, "Lenovo IdeaPad 3 Core i3 256GB SSD", "LNV-IP3-I3", "Computers", 38000.0, 46000.0, 0, 3, "Redington Kenya", "Intel Core i3 11th Gen, Abyss Blue", "Out of Stock"),
            Product(14, 1, "Logitech M185 Wireless Optical Mouse", "LOG-M185-GRY", "Computer Accessories", 1100.0, 1800.0, 22, 5, "Logitech Official", "2.4GHz with nano receiver, 12mo battery", "In Stock"),
            Product(15, 1, "SanDisk Ultra 64GB MicroSD Card 100MB/s", "SND-MSD-64G", "Storage", 750.0, 1400.0, 35, 8, "SanDisk Distro", "Class 10 A1 Speed rated", "In Stock"),
            Product(16, 1, "SanDisk Extreme 1TB Portable SSD", "SND-SSD-1TB", "Storage", 14000.0, 19500.0, 3, 3, "SanDisk Distro", "1050MB/s Read speed, rugged IP55", "Low Stock"),
            Product(17, 1, "TP-Link Archer C6 Gigabit WiFi Router", "TPL-C6-AC1200", "Networking", 3900.0, 5800.0, 11, 4, "TP-Link East Africa", "AC1200 MU-MIMO Dual Band 4 Antennas", "In Stock"),
            Product(18, 1, "Safaricom 4G Portable MiFi Router", "SAF-MIFI-4G", "Networking", 3200.0, 4500.0, 16, 5, "Safaricom Dealer", "Up to 150Mbps, 10 user connection", "In Stock"),
            Product(19, 1, "HDMI 2.1 4K 120Hz Ultra Cable 2m", "CAB-HDMI21-2M", "Accessories", 600.0, 1200.0, 28, 5, "Ugreen Importers", "48Gbps Bandwidth, HDR 10+", "In Stock"),
            Product(20, 1, "Ring Light 12-inch + 2.1m Metal Tripod", "LGT-RNG-12IN", "Content Creation", 1800.0, 3200.0, 14, 4, "Studio Supplies", "3 Light Modes + Stepless Dimming", "In Stock"),
            Product(21, 1, "Laptop Cooling Pad 6 Silent LED Fans", "ACC-COOL-6FN", "Computer Accessories", 1500.0, 2800.0, 9, 3, "Havit Tech", "Dual USB ports, adjustable ergonomic stand", "In Stock"),
            Product(22, 1, "Universal Surge Extension 4 Gang + 4 USB", "EXT-SURGE-4G", "Power & Electrical", 1200.0, 2400.0, 20, 5, "Ldnio Supplies", "Fireproof PC shell, 2500W rated", "In Stock"),
            Product(23, 1, "Tempered Glass Screen Protector 9D Curve", "GLS-9D-UNIV", "Protection", 120.0, 500.0, 80, 20, "Guangzhou Direct", "High clarity scratch-resistant 9H glass", "In Stock"),
            Product(24, 1, "Shockproof Matte Matte Case Assorted", "CSE-SHK-ASST", "Protection", 250.0, 700.0, 60, 15, "Guangzhou Direct", "Anti-fingerprint TPU bumper case", "In Stock"),
            Product(25, 1, "100W Monocrystalline Solar Panel", "SLR-PNL-100W", "Solar & Energy", 6500.0, 9500.0, 4, 3, "Chloride Exide Partner", "High efficiency Grade A solar cells", "Low Stock"),
            Product(26, 1, "Chloride Exide 12V 100Ah Deep Cycle Battery", "BAT-CE-100AH", "Solar & Energy", 19500.0, 26000.0, 5, 2, "Chloride Exide Kenya", "Maintenance free solar storage battery", "In Stock"),
            Product(27, 1, "Smart Watch Ultra 2 with Orange Strap", "WCH-ULT2-ORG", "Wearables", 2900.0, 4800.0, 15, 4, "Shenzhen Tech", "Heart rate, sports tracking, wireless charge", "In Stock"),
            Product(28, 1, "DJI Osmo Mobile SE Smartphone Gimbal", "DJI-OM-SE", "Content Creation", 13000.0, 18000.0, 0, 2, "DJI Regional", "3-Axis stabilization, ActiveTrack 6.0", "Out of Stock"),
            Product(29, 1, "Smartphone Screen Replacement Service", "SRV-SCR-REP", "Services", 800.0, 2500.0, 999, 10, "In-house Repair Lab", "Screen glass & digitizer installation labour", "In Stock"),
            Product(30, 1, "Laptop Windows OS & Software Tuning", "SRV-LPT-TUNE", "Services", 0.0, 1500.0, 999, 10, "In-house Tech Support", "Diagnostics, clean OS install & drivers", "In Stock")
        )
        dao.insertProducts(products)

        // 3. 20 Realistic Kenyan Customers
        val customers = listOf(
            Customer(1, 1, "David Mutua", "+254 722 101010", "david.mutua@gmail.com", "Westlands, Nairobi", "Retail", 91000.0, 0.0, "Frequent phone buyer, pays via M-PESA"),
            Customer(2, 1, "Beatrice Achieng", "+254 733 202020", "achieng.b@outlook.com", "Kilimani, Nairobi", "Retail", 34500.0, 7500.0, "Bought JBL speaker on credit, pending balance"),
            Customer(3, 1, "Kevin Kiprop", "+254 711 303030", "kevin.kiprop@eldoret.biz", "Eldoret Town", "Wholesale", 185000.0, 28000.0, "Resells phone accessories in Eldoret"),
            Customer(4, 1, "Grace Wanjiku", "+254 724 404040", "wanjiku.g@salon.co.ke", "CBD, Tom Mboya St", "Retail", 16800.0, 0.0, "Runs beauty salon, buys ring lights"),
            Customer(5, 1, "Brian Omondi", "+254 701 505050", "omondi.brian@devke.io", "South B, Nairobi", "Retail", 72500.0, 0.0, "Software engineer, bought HP laptop"),
            Customer(6, 1, "Faith Chebet", "+254 728 606060", "chebet.f@nakuru.org", "Nakuru City", "Retail", 21500.0, 4500.0, "Bought Samsung A15, promised payment next week"),
            Customer(7, 1, "Samuel Kamau", "+254 715 707070", "kamau.samuel@kamauelec.com", "Nyamakima, Nairobi", "Wholesale", 240000.0, 45000.0, "Wholesale cables and surge protectors"),
            Customer(8, 1, "Mercy Ndinda", "+254 720 808080", "m.ndinda@machakos.ac.ke", "Machakos", "Retail", 14200.0, 0.0, "Teacher, bought Oraimo powerbank & earphones"),
            Customer(9, 1, "Dennis Mwangi", "+254 718 909090", "dennis.mwangi@gmail.com", "Thika Town", "Retail", 36000.0, 12000.0, "Sony headphones deposit, pending balance"),
            Customer(10, 1, "Esther Njeri", "+254 727 010102", "esther.njeri@safari.com", "Karen, Nairobi", "Retail", 69500.0, 0.0, "Bought iPhone 13, paid full M-PESA"),
            Customer(11, 1, "Collins Cheruiyot", "+254 714 121212", "c.cheruiyot@kapsabet.ke", "Kapsabet", "Retail", 35500.0, 0.0, "Solar panel and deep cycle battery"),
            Customer(12, 1, "Amina Hassan", "+254 725 232323", "amina.hassan@mombasatech.ke", "Mombasa Old Town", "Corporate", 112000.0, 18500.0, "Mombasa branch networking supplier"),
            Customer(13, 1, "Peter Njoroge", "+254 721 343434", "njoroge.peter@gmail.com", "Kikuyu, Kiambu", "Retail", 8500.0, 0.0, "Router and cables installation"),
            Customer(14, 1, "Cynthia Wangari", "+254 705 454545", "wangari.cynthia@media.co.ke", "Kileleshwa, Nairobi", "Retail", 26200.0, 0.0, "Content creator, bought ring light & tripod"),
            Customer(15, 1, "John Otieno", "+254 719 565656", "otieno.john@kisumu.go.ke", "Kisumu City", "Retail", 4200.0, 0.0, "Oraimo earbuds replacement"),
            Customer(16, 1, "Mary Moraa", "+254 723 676767", "moraa.mary@kisii.net", "Kisii Town", "Retail", 19500.0, 6000.0, "SanDisk 1TB SSD credit sale"),
            Customer(17, 1, "Patrick Gitau", "+254 710 787878", "gitau.patrick@ruiru.com", "Ruiru Bypass", "Retail", 27999.0, 0.0, "Redmi Note 13 paid via Bank"),
            Customer(18, 1, "Yvonne Atieno", "+254 729 898989", "yvonne.atieno@legal.co.ke", "Upper Hill, Nairobi", "Corporate", 4800.0, 0.0, "Smart Watch Ultra 2 gift"),
            Customer(19, 1, "George Kimani", "+254 713 909091", "george.kimani@agro.co.ke", "Naivasha", "Retail", 35500.0, 15000.0, "Solar installation equipment"),
            Customer(20, 1, "Lucy Wambui", "+254 726 012345", "lucy.wambui@cbdretail.ke", "Luthuli Avenue, Nairobi", "Wholesale", 154000.0, 0.0, "Fast screen protectors & cases bulk client")
        )
        dao.insertCustomers(customers)

        // 4. Employees
        val employees = listOf(
            Employee(1, 1, "Kelvin Mworia", "kelvin@techpoint-ke.com", "+254 712 345678", "Owner", "Active"),
            Employee(2, 1, "Sharon Achieng", "sharon@techpoint-ke.com", "+254 720 112233", "Manager", "Active"),
            Employee(3, 1, "Victor Kiprono", "victor@techpoint-ke.com", "+254 715 445566", "Salesperson", "Active"),
            Employee(4, 1, "Hellen Wanjiru", "hellen@techpoint-ke.com", "+254 723 778899", "Accountant", "Active")
        )
        dao.insertEmployees(employees)

        // 5. Recent Sales Transactions
        val sales = listOf(
            Sale(1, 1, "TPE-2026-0081", 1, "David Mutua", 21500.0, 0.0, 2965.5, 21500.0, "M-PESA", "Paid", "QK84X9J21L", "Victor Kiprono", now - 2 * 3600000L),
            Sale(2, 1, "TPE-2026-0082", 10, "Esther Njeri", 69500.0, 1000.0, 9448.2, 68500.0, "M-PESA", "Paid", "QK89R4M81Z", "Sharon Achieng", now - 5 * 3600000L),
            Sale(3, 1, "TPE-2026-0083", null, "Walk-in Customer", 5500.0, 0.0, 758.6, 5500.0, "Cash", "Paid", "", "Victor Kiprono", now - 7 * 3600000L),
            Sale(4, 1, "TPE-2026-0084", 2, "Beatrice Achieng", 15500.0, 0.0, 2137.9, 15500.0, "Credit", "Credit", "", "Sharon Achieng", now - 1 * dayMillis),
            Sale(5, 1, "TPE-2026-0085", 17, "Patrick Gitau", 27999.0, 500.0, 3793.0, 27499.0, "Bank", "Paid", "BNK-661029", "Kelvin Mworia", now - 1 * dayMillis - 3 * 3600000L),
            Sale(6, 1, "TPE-2026-0086", 4, "Grace Wanjiku", 3200.0, 0.0, 441.4, 3200.0, "M-PESA", "Paid", "QK72P1K99X", "Victor Kiprono", now - 2 * dayMillis),
            Sale(7, 1, "TPE-2026-0087", 7, "Samuel Kamau", 48000.0, 3000.0, 6206.9, 45000.0, "Credit", "Credit", "", "Kelvin Mworia", now - 3 * dayMillis),
            Sale(8, 1, "TPE-2026-0088", 5, "Brian Omondi", 64500.0, 1500.0, 8689.6, 63000.0, "Card", "Paid", "TX-VISA-8812", "Sharon Achieng", now - 4 * dayMillis),
            Sale(9, 1, "TPE-2026-0089", 8, "Mercy Ndinda", 4200.0, 200.0, 551.7, 4000.0, "M-PESA", "Paid", "QK61A0M43D", "Victor Kiprono", now - 5 * dayMillis),
            Sale(10, 1, "TPE-2026-0090", 14, "Cynthia Wangari", 6400.0, 0.0, 882.8, 6400.0, "M-PESA", "Paid", "QK59X8L22P", "Sharon Achieng", now - 6 * dayMillis)
        )
        dao.insertSales(sales)

        // Sale Items
        val saleItems = listOf(
            SaleItem(1, 1, 1, "Samsung Galaxy A15 128GB", 1, 21500.0, 21500.0),
            SaleItem(2, 2, 3, "iPhone 13 128GB Midnight (Grade A)", 1, 69500.0, 69500.0),
            SaleItem(3, 3, 5, "Anker 65W GaNPrime Fast Charger", 1, 5500.0, 5500.0),
            SaleItem(4, 4, 9, "JBL Flip 6 Bluetooth Speaker (Teal)", 1, 15500.0, 15500.0),
            SaleItem(5, 5, 2, "Redmi Note 13 256GB", 1, 27999.0, 27999.0),
            SaleItem(6, 6, 20, "Ring Light 12-inch + 2.1m Metal Tripod", 1, 3200.0, 3200.0),
            SaleItem(7, 7, 7, "Type-C to Type-C Braided Cable 1.5m", 30, 800.0, 24000.0),
            SaleItem(8, 7, 22, "Universal Surge Extension 4 Gang + 4 USB", 10, 2400.0, 24000.0),
            SaleItem(9, 8, 12, "HP 250 G9 Core i5 8GB 512GB SSD", 1, 64500.0, 64500.0),
            SaleItem(10, 9, 11, "Oraimo FreePods 4 ANC Earbuds", 1, 4200.0, 4200.0),
            SaleItem(11, 10, 4, "Oraimo 20000mAh Powerbank Toast 10", 2, 3200.0, 6400.0)
        )
        dao.insertSaleItems(saleItems)

        // 6. Expenses
        val expenses = listOf(
            Expense(1, 1, "Digital Mall Shop Rent (September)", "Rent", 45000.0, now - 3 * dayMillis, "Bank", "Moi Avenue Shop G14 monthly rent"),
            Expense(2, 1, "Safaricom Fibre for Business 50Mbps", "Internet", 5500.0, now - 8 * dayMillis, "M-PESA", "High speed optical fiber broadband"),
            Expense(3, 1, "Kenya Power Electricity Bill (KPLC)", "Electricity", 3850.0, now - 5 * dayMillis, "M-PESA", "Tokens for shop showroom lighting and AC"),
            Expense(4, 1, "Staff Weekly Commission & Transport", "Transport", 8000.0, now - 2 * dayMillis, "M-PESA", "Victor & Sharon sales commission"),
            Expense(5, 1, "Stock Logistics & Clearing from Airport", "Stock", 12500.0, now - 10 * dayMillis, "Bank", "Clearance for Anker & Oraimo consignments"),
            Expense(6, 1, "Instagram & TikTok Sponsored Ads", "Marketing", 6000.0, now - 4 * dayMillis, "M-PESA", "Promo campaign for back-to-school laptops"),
            Expense(7, 1, "Shop Security & Service Charge", "Maintenance", 3500.0, now - 15 * dayMillis, "M-PESA", "Mall management association dues")
        )
        dao.insertExpenses(expenses)

        // 7. Debts / Credit Customers
        val debts = listOf(
            Debt(1, 1, 7, "Samuel Kamau", "+254 715 707070", 7, 45000.0, 0.0, 45000.0, now - 2 * dayMillis, "Overdue", "Wholesale stock, promised payment was Monday", now - 12 * dayMillis),
            Debt(2, 1, 3, "Kevin Kiprop", "+254 711 303030", null, 38000.0, 10000.0, 28000.0, now + 5 * dayMillis, "Partially Paid", "Supplied Eldoret shop with audio accessories", now - 7 * dayMillis),
            Debt(3, 1, 9, "Dennis Mwangi", "+254 718 909090", null, 18000.0, 6000.0, 12000.0, now + 3 * dayMillis, "Pending", "Deposit paid on Sony headphones", now - 4 * dayMillis),
            Debt(4, 1, 2, "Beatrice Achieng", "+254 733 202020", 4, 15500.0, 8000.0, 7500.0, now + 7 * dayMillis, "Partially Paid", "JBL Flip 6 balance due end of month", now - 1 * dayMillis),
            Debt(5, 1, 6, "Faith Chebet", "+254 728 606060", null, 4500.0, 0.0, 4500.0, now - 1 * dayMillis, "Overdue", "Samsung A15 case and earphones balance", now - 8 * dayMillis),
            Debt(6, 1, 16, "Mary Moraa", "+254 723 676767", null, 6000.0, 0.0, 6000.0, now + 10 * dayMillis, "Pending", "Portable SSD credit", now - 2 * dayMillis),
            Debt(7, 1, 19, "George Kimani", "+254 713 909091", null, 15000.0, 0.0, 15000.0, now + 12 * dayMillis, "Pending", "Solar battery balance pending site delivery", now - 1 * dayMillis)
        )
        dao.insertDebts(debts)

        val debtPayments = listOf(
            DebtPayment(1, 2, 10000.0, "M-PESA", "QK81M209A", now - 3 * dayMillis),
            DebtPayment(2, 3, 6000.0, "M-PESA", "QK83N881B", now - 2 * dayMillis),
            DebtPayment(3, 4, 8000.0, "Cash", "REC-041", now - 1 * dayMillis)
        )
        debtPayments.forEach { dao.insertDebtPayment(it) }

        // 8. Invoices
        val invoices = listOf(
            Invoice(1, 1, "INV-2026-0038", 12, "Amina Hassan", "amina.hassan@mombasatech.ke", "+254 725 232323", 96551.7, 15448.3, 112000.0, now - 14 * dayMillis, now - 1 * dayMillis, "Overdue", "Mombasa office network setup & routers"),
            Invoice(2, 1, "INV-2026-0039", 5, "Brian Omondi", "omondi.brian@devke.io", "+254 701 505050", 55603.4, 8896.6, 64500.0, now - 7 * dayMillis, now + 7 * dayMillis, "Paid", "HP Laptop & wireless setup"),
            Invoice(3, 1, "INV-2026-0040", 7, "Samuel Kamau", "kamau.samuel@kamauelec.com", "+254 715 707070", 38793.1, 6206.9, 45000.0, now - 3 * dayMillis, now + 11 * dayMillis, "Sent", "Bulk charging accessories purchase")
        )
        dao.insertInvoices(invoices)

        val invoiceItems = listOf(
            InvoiceItem(1, 1, "TP-Link Archer C6 Dual Band Gigabit Router (x10)", 10, 5800.0, 58000.0),
            InvoiceItem(2, 1, "Safaricom 4G Portable MiFi Router (x12)", 12, 4500.0, 54000.0),
            InvoiceItem(3, 2, "HP 250 G9 Core i5 8GB 512GB SSD Laptop", 1, 64500.0, 64500.0),
            InvoiceItem(4, 3, "Type-C Braided Fast Cables 100W Bulk Pack (x30)", 30, 800.0, 24000.0),
            InvoiceItem(5, 3, "Universal Surge Protector Extension 4-Way (x10)", 10, 2400.0, 24000.0)
        )
        dao.insertInvoiceItems(invoiceItems)

        // 9. Stock Adjustments Audit History
        val stockAdjustments = listOf(
            StockAdjustment(1, 1, "Samsung Galaxy A15 128GB", "Stock In", 20, 0, 20, "Supplier shipment from Mitsumi Kenya", now - 20 * dayMillis),
            StockAdjustment(2, 12, "HP 250 G9 Core i5 8GB 512GB SSD", "Stock In", 10, 0, 10, "Direct import consignment", now - 15 * dayMillis),
            StockAdjustment(3, 13, "Lenovo IdeaPad 3 Core i3 256GB SSD", "Stock Out", -1, 1, 0, "Last unit sold on display", now - 2 * dayMillis),
            StockAdjustment(4, 28, "DJI Osmo Mobile SE Smartphone Gimbal", "Recount", -1, 1, 0, "Inventory audit reconciliation", now - 1 * dayMillis),
            StockAdjustment(5, 3, "iPhone 13 128GB Midnight (Grade A)", "Damaged", -1, 4, 3, "Slight housing scratch, moved to discount section", now - 5 * dayMillis)
        )
        dao.insertStockAdjustments(stockAdjustments)

        // 10. Notifications
        val notifications = listOf(
            NotificationItem(1, 1, "Out of Stock Alert", "Lenovo IdeaPad 3 (LNV-IP3-I3) is completely sold out! Restock soon.", "stock", false, now - 1 * 3600000L),
            NotificationItem(2, 1, "Overdue Debt Reminder", "Samuel Kamau has an overdue balance of KES 45,000 due 2 days ago.", "debt", false, now - 3 * 3600000L),
            NotificationItem(3, 1, "Payment Received via M-PESA", "Received KES 21,500 from David Mutua (Ref: QK84X9J21L).", "payment", true, now - 4 * 3600000L),
            NotificationItem(4, 1, "Low Stock Warning", "Apple 20W USB-C Adapters only have 2 units remaining (Minimum threshold: 5).", "stock", false, now - 8 * 3600000L),
            NotificationItem(5, 1, "Invoice Payment Due", "Invoice INV-2026-0038 for Amina Hassan is overdue by 1 day.", "invoice", true, now - 18 * 3600000L)
        )
        dao.insertNotifications(notifications)
    }
}
