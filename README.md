### API for UI testing of MovieMates mobile app

Used tools:
- _Java_
- _Appium API_

Given code represents working prototype of API for UI testing of social network hybrid mobile app. Choice of Appium has been determined by needs to provide platform-independent testing on both iOS and Android.

In many cases, locating and interaction with UI elements is executing through Element class - wrapper for native UI elements. This allows to store element locator (simple or complex) as property of the class, and by that refer to the element using Element class instance. The main reason of this approach is that Appium, as derivative of Selenium, does not provide direct way to determine if requested element exists or not. Wrapping elements allows to handle thrown `NoSuchElementException` in grace manner, obtaining current status of element. This is especially important as the app relies on scrolling heavily, and each scroll re-renders elements in _XML_ tree, what leads to "staling" element references.

Mixed locator strategy is used in API. Locating elements by their IDs used whenever it's possible, though in some cases locating by _XPath_ takes over as the most reliable method.

The nature of mobile page layout puts significant limitations on how to interact with its elements. Unlike web pages, there is no easy way in native app to obtain whole _XML_ tree of displaying page, what means API can only have access to visible elements in current viewport. Such limitations has determined transition from popular _Page Object Model_ to implementation of compounded UI objects such as `MoviePicker` or `TimesSection`. These objects hide implementation of elements locating, scrolling etc. and provide UI functional interaction in form of `MoviePicker.findMovie(movieName)`, `Cinema.isTimeAvailable(time)` etc.

`PlatformDriver` class uses device UUID provided as argument to fetch information from _JSON_ dataset relevant to the device and run tests in accordance to device's characteristics (platform, OS version etc.)
