TeamCity-Inspection-Notification-Plugin
=======================================

TeamCity server-side plugin that notifies committers about newly introduced code inspection violations.

![Screen shot of app](images/email.png "Screen shot of the notification")

# Installation

[Download](https://github.com/frimtec/teamcity-inspection-notification-plugin/releases/latest) the ZIP file release, 
and upload it, as an administrator, to your TeamCity server.

Compatibility:
* Release 1.0.1 was tested against TeamCity 2018.2.

# Configuration

On TeamCity, as an administrator, configure the plugin on the administration tab "Inspection Violation Notfification".

# Future Improvements

* Replace own email SMTP configuration.
* Replace use of deprecated SQLRunner API.

# Change log

## Version 1.0.1
* Initial release.
