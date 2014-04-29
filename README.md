**Welcome to Refill: THE Prescription Management Solution for the 21st Century!**

This project requires the android-support-v7-appcompat project to be imported into the project. This can be done in Eclipse by going to File -> Import -> Existing Android Code Into Workspace -> Browse to <sdkdirectory>\extras\android\support\v7\appcompat -> Hit OK.

Next, Right click on the Refill project and select Properties -> Android.

Android 4.4.2 should be the only one checked for the Project Build Target, and in the Library section you need to add the appcompat project you just imported. Make sure Is Library is unchecked!

Next, navigate to Properties -> Java Build Path. The Projects tab should have nothing but this android-support-v7-appcompat folder, Libraries tab should have android-support-v4.jar and android-support-v7-appcompat.jar, and Order & Export should look like:
![Alt text](refill/raw/master/buildpath.png?raw=true "Order & Export Build Path")

You should now be good to go to launch & edit Refill!

Features
--------
* **Manage detailed prescription info for yourself and others**
* **Control over your database of pharmacies, doctors, and patients**
* **Notifications a few days before & on days of Refills**
* **Easily contact a pharmacy/doctor for any prescription**
* **Customization of display settings & other components**
* **Schedule to easily view when to take pills weekly**

To Do
--------
* Automatically contact pharmacies/doctors on days necessary
* Improve our implementation of a user's weekly schedule (notifications, for example)