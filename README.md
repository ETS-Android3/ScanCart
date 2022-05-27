# ScanCart

This is the sample repository for simple scan and pay e-commerce app. Where user can just scan the product and add it to the cart.

Please watch [this video](https://youtu.be/e7rDq12xq_g) for the demo.

APK is provided in [this link.](https://github.com/kgmalvi/ScanCart/blob/master/APK/ScanCart_Version_01.00.00-01.apk)

Assumptions:
- This is the MVVM based application creating using Android JetPack components.
- For QR Code scanning, no libraries has been used. Google's ML kit is used in combination with CameraX API to increase efficiency and reduce app size.
- Although there's more scope for additional efficiency. 
- Although this is the app used by customer, as there's no admin account to handle new products, the functionality is implemented in same application.
- There are 2 ways to add product to the cart,
  - One way is scan the QR: Using this way, if the product is not added to DB then first app will ask for product details, and then it will be added to the cart. If the scanned product is there in DB then product will be directly added to the cart.
  - All the products in DB will be listed on Home screen, and user can select the product from home screen to add to the DB.
- The invoice is created with lot of static and only few dynamic fields. Dynamic fields mainly includes, date, time, cart items, total amount.
- For the simplicity, it's assumed for now that user will add only 1 quantity per product.
