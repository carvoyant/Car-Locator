#Car Locator Walkthrough
Car Locator is a native android application that helps you locate your Carvoyant enabled car (A Carvoyant dongle with API key is required). It was also designed as a basic template and guideline for creating your own Carvoyant enabled application.

##Environment Setup

###IDE and SDK
In addition to the standard Android development environment, two libraries need to be added to support API level compatability and Google Maps services.


+ Download the Android SDK, install Eclipse and the ADT plugin (if you're using Eclipse).  Using the Android SDK manager, install the latest SDK tools, platforms and corresponding google APIs packages, as well as the Android Support Library and Google Play Services packages.  [Building Your First App](http://developer.android.com/training/basics/firstapp/index.html)

+ Import the Appcompat_v7 and Google Play Services projects into your workspace, reference the libraries in your main Android project (Car Locator), and include the libs in your main Android project's build path.  [Adding libraries with resources](https://developer.android.com/tools/support-library/setup.html)

###Strings.xml
There are values within this file that must be unique to your application.  

+ You must provide your own [Google Maps API key](https://developers.google.com/maps/documentation/android/start#getting_the_google_maps_android_api_v2). 

+ A Carvoyant supplied client ID is also required, as well as a properly formed "redirect_uri" that is explained below under "OAuth.java"



##Application and OAuth2 implementation
The Carvoyant API implements an OAuth2 security model.  In order to make successful API calls, the application must pass an access token to the Carvoyant servers.  To obtain a valid access token the application must prompt the user for their Carvoyant credentials.  This is done by directing the user to a Carvoyant login service via the user's browser.  The Carvoyant login screen will process the request and upon successful validation, will redirect the user's browser to a predefined URL containing the access token that will be processed by the application. Details on how this process is specifically implemented in the Car-Locator application is outlined below.

###AndroidManifest.xml
Inside the manifest our Google API key is defined and our Activities are declared.  The LaunchActivity is declared to be the launching activity while the OAuth activity is set up with an intent filter.  This intent filter allows the activity to be launched by a browser which is necessary in our OAuth implementation.

###LaunchActivity.java
This Activity exists only because a separate Activity was needed to distinguish the applications main launch Activity (LaunchActivity.java), from the Activity that contains the browsable intent filter (OAuth.java).  All it does it open the OAuth activity (described below) which will begin security validation.

###OAuth.java
This is the activity that is launched from the both the launch activity and the intent filter defined in the manifest.  This activity will first check for a stored token or if there is an incoming URL to parse a token from.  If neither is the case, then the user will be prompted with a screen which will forward them to the Carvoyant login page.  The URL for the Carvoyant login page have "client_id" and redirect_uri" parameters that needs to be included.  The client ID is unique to your application and is provided by Carvoyant.  The redirect URI needs to be formatted so that when the user successfully authenticates, the redirect from the Carvoyant server will be picked up by the intent filter and the OAuth activity will be launched.  In our example, this formatted redirect URI matches the data scheme defined in the Android Manifest (carlocator://...).  Upon redirect the OAuth activity will store the incoming token and then launch the CarLocator Activity, which represents the main functionality of the application.

###CarLocator.java
This is the main Activity of the application.  It makes calls to the Carvoyant API and uses the driver's vehicle data to plot their location on a Google map.
 

