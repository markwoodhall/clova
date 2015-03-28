(ns clova.core-test
  (:require [clova.core :as core]
            #+clj [clojure.test :refer [is deftest testing are]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing are]]))

(deftest email-validator
  (testing "validating a valid email address"
    (doseq [email ["test@googlemail.com" "test+test@googlemail.com"]]
      (is (core/valid-email? email))))

  (testing "validating an invalid email address"
    (doseq [email ["testing" "test@.googlemail.com" "@googlemail.com"]]
      (is (not (core/valid-email? email))))))
