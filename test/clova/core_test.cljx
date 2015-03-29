(ns clova.core-test
  (:require [clova.core :as core]
            #+clj [clojure.test :refer [is deftest testing are]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing are]]))

(deftest email-validator
  (testing "validating a valid email address"
    (doseq [email ["test@googlemail.com" "test+test@googlemail.com"]]
      (is (core/email? email))))

  (testing "validating an invalid email address"
    (doseq [email [100 {:a 1} [1 2] "testing" "test@.googlemail.com" "@googlemail.com"]]
      (is (not (core/email? email))))))

(deftest zip-code-validator
  (testing "validating a valid zip code"
    (doseq [zip (concat (range 96801 96830) (map str (range 96801 96830)))]
      (is (core/zip-code? zip))))

  (testing "validating an invalid zip code"
    (doseq [zip ["abc" 100 {:a 1} [1 2] "1-1-0"]]
      (is (not (core/zip-code? zip))))))

(deftest post-code-validator
  (testing "validating a valid uk post code"
    (doseq [post-code ["B11 2SB" "b11 2sb"]]
      (is (core/post-code? post-code))))

  (testing "validating an invalid uk post code"
    (doseq [post-code ["abc" 100 {:a 1} [1 2] "1-1-0" "B112SB" "b112sb"]]
      (is (not (core/post-code? post-code))))))
