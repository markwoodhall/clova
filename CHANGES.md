## 0.7.0 (20-11-2015)

* Add :allow-missing-key? to enable default behavious where validators do not fail if a key is not present.
* Add `required` function to disable :allow-missing-key? and force a key to be present.

## 0.6.0 (19-11-2015)

* Add longer? validator.
* Add shorter? validator.

## 0.5.0 (19-11-2015)

* Fixed issue where comparing a nil 'value' with numeric type validators would cause a NullPointerException.
* Add option to pass :default-message-fn to validate in order to support custom validation messages.
