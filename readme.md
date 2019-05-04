TeamCity-Inspection-Notification-Plugin
=======================================

TeamCity server-side plugin that notifies committers about newly introduced code inspection violations.

![Screen shot of app](images/email.png "Screen shot of the notification")

# Installation

[Download](https://github.com/frimtec/teamcity-inspection-notification-plugin/releases/latest) the ZIP file release, drop it in your TeamCity installation's `.BuildServer/plugins/`
directory (as explained by [Jetbrains](http://www.jetbrains.com/teamcity/plugins/)) and restart the server.

Compatibility:
* Release 1.0.0 was tested against TeamCity 2018.2.

# Configuration

On TeamCity, as an administrator, configure the plugin on the administration tab "Inspection Violation Notfification".

# Future Improvements

* Replace own email SMTP configuration.

# Change log

## Version 1.0.0
* First release.