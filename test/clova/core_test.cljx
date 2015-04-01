(ns clova.core-test
  (:require [clova.core :as core]
            #+clj [clojure.test :refer [is deftest testing are]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing are]]))

(let [only-clova-meta #(select-keys % [:type :default-message])
      only-clova-set-meta #(select-keys % [:type :target :default-message])
      exp-email-meta {:type :email :target :email :default-message "Email address %s is invalid."}
      exp-post-meta {:type :post-code :target :post-code :default-message "Post code %s is invalid."}
      exp-zip-meta {:type :zip-code :target :zip-code :default-message "Zip code %s is invalid."}]

  (deftest email-validator
    (testing "email validator exposes correct meta data"
      (is (= (dissoc exp-email-meta :target)
             (only-clova-meta (meta core/email?)))))

    (testing "validating a valid email address"
      (doseq [email ["test@googlemail.com" "test+test@googlemail.com"]]
        (is (core/email? email))))

    (testing "validating an invalid email address"
      (doseq [email [100 {:a 1} [1 2] "testing" "test@.googlemail.com" "@googlemail.com"]]
        (is (not (core/email? email))))))

  (deftest zip-code-validator
    (testing "zip code validator exposes correct meta data"
      (is (= (dissoc exp-zip-meta :target)
             (only-clova-meta (meta core/zip-code?)))))

    (testing "validating a valid zip code"
      (doseq [zip (concat (range 96801 96830) (map str (range 96801 96830)))]
        (is (core/zip-code? zip))))

    (testing "validating an invalid zip code"
      (doseq [zip ["abc" 100 {:a 1} [1 2] "1-1-0"]]
        (is (not (core/zip-code? zip))))))

  (deftest post-code-validator
    (testing "post code validator exposes correct meta data"
      (is (= (dissoc exp-post-meta :target)
             (only-clova-meta (meta core/post-code?)))))

    (testing "validating a valid uk post code"
      (doseq [post-code ["B11 2SB" "b11 2sb"]]
        (is (core/post-code? post-code))))

    (testing "validating an invalid uk post code"
      (doseq [post-code ["abc" 100 {:a 1} [1 2] "1-1-0" "B112SB" "b112sb"]]
        (is (not (core/post-code? post-code))))))

  (deftest validation-set
    (testing "testing a validation set returns a sequence of the correct
             validation functions"
      (let [v-set (core/validation-set [:email core/email?
                                        :zip-code core/zip-code?
                                        :post-code core/post-code?])
            email-meta (meta (first v-set))
            zip-meta (meta (second v-set))
            post-code-meta (meta (nth v-set 2))]
        (is (= exp-email-meta (only-clova-set-meta email-meta)))
        (is (= exp-zip-meta (only-clova-set-meta zip-meta)))
        (is (= exp-post-meta (only-clova-set-meta post-code-meta))))))

  (deftest validation
    (testing "testing validation using a validation set returns
             a valid? = false result and a sequence of the validation results"
      (let [v-set (core/validation-set [:email core/email? :post-code core/post-code?])
            result (core/validate v-set {:email "abc" :post-code 12})]
        (is (not (:valid? result)))
        (is (= "Email address abc is invalid." (first (:results result))))
        (is (= "Post code 12 is invalid." (second (:results result))))))

    (testing "testing validation using a validation set returns
             a valid? = true result and no validation results"
      (let [v-set (core/validation-set [:email core/email?])
            result (core/validate v-set {:email "test.email@googlemail.com"})]
        (is (:valid? result))
        (is (empty? (:results result)))))))
