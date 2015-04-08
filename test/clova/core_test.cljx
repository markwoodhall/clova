(ns clova.core-test
  (:require [clova.core :as core]
            #+clj [clojure.test :refer [is deftest testing are]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing are]]))

(let [only-clova-meta #(select-keys % [:type :default-message])
      only-clova-set-meta #(select-keys % [:type :target :default-message :args])
      exp-email-meta {:type :email :target :email :default-message "%s is an invalid value for %s."}
      exp-post-meta {:type :post-code :target :post-code :default-message "%s is an invalid value for %s."}
      exp-url-meta {:type :url :target :url :default-message "%s is an invalid value for %s."}
      exp-between-meta {:type :between :args [1 9] :target :age :default-message "%s is an invalid value for %s, it must be between %s and %s."}
      exp-matches-meta {:type :matches :args [#"\w*"] :target :matches :default-message "%s is an invalid value for %s."}
      exp-zip-meta {:type :zip-code :target :zip-code :default-message "%s is an invalid value for %s."}]

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

  (deftest url-validator
    (testing "url validator exposes correct meta data"
      (is (= (dissoc exp-url-meta :target)
             (only-clova-meta (meta core/url?)))))

    (testing "validating a valid url "
      (doseq [url ["http://google.com" "https://www.google.com"]]
        (is (core/url? url))))

    (testing "validating an invalid url"
      (doseq [url ["aaaaasnnnnxnxx.c" "httpp://www.google.com"]]
        (is (not (core/url? url))))))

  (deftest between-validator
    (testing "between validator exposes correct meta data"
      (is (= (dissoc exp-between-meta :target :args)
             (only-clova-meta (meta core/between?)))))

    (testing "validating a valid between value"
      (doseq [between [1 2 3 4 5 6 7 8 9]]
        (is (core/between? between 1 9))))

    (testing "validating an invalid between"
      (doseq [between [0 10 11 12 20 30 40]]
        (is (not (core/between? between 1 9))))))

  (deftest matches-validator
    (testing "matches validator exposes correct meta data"
      (is (= (dissoc exp-matches-meta :target :args)
             (only-clova-meta (meta core/matches?)))))

    (testing "validating a value that matches"
      (is (core/matches? "amatch" #"amatch")))

    (testing "validating a value that does not match"
      (is (not (core/matches? "nonmatch" #"amatch")))))

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
        (is (= exp-post-meta (only-clova-set-meta post-code-meta)))))

    (testing "testing a validation set with multi arity returns a sequence of the correct
             validation functions"
      (let [v-set (core/validation-set [:age [core/between? 1 9]])
            between-meta (meta (first v-set))]
        (is (= exp-between-meta (only-clova-set-meta between-meta))))))

  (deftest validation
    (testing "testing validation using a validation set returns
             a valid? = false result and a sequence of the validation results"
      (let [v-set (core/validation-set [:email core/email?
                                        :post-code core/post-code?
                                        :zip-code core/zip-code?
                                        :matches [core/matches? #"amatch"]
                                        :url core/url?
                                        :age [core/between? 18 40]
                                        [:nested :value] [core/between? 1 10]])
            result (core/validate v-set {:email "abc"
                                         :post-code 12
                                         :zip-code "abc"
                                         :matches "nomatch"
                                         :url "abc"
                                         :age 10
                                         :nested {:value 0}})]
        (is (not (:valid? result)))
        (is (= "abc is an invalid value for email." (first (:results result))))
        (is (= "12 is an invalid value for post-code." (second (:results result))))
        (is (= "abc is an invalid value for zip-code." (nth (:results result) 2)))
        (is (= "nomatch is an invalid value for matches." (nth (:results result) 3)))
        (is (= "abc is an invalid value for url." (nth (:results result) 4)))
        (is (= "10 is an invalid value for age, it must be between 18 and 40." (nth (:results result) 5)))
        (is (= "0 is an invalid value for nested value, it must be between 1 and 10." (nth (:results result) 6)))))

    (testing "testing validation using a validation set returns
             a valid? = true result and no validation results"
      (let [v-set (core/validation-set [:email core/email?
                                        :post-code core/post-code?
                                        :zip-code core/zip-code?
                                        :matches [core/matches? #"amatch"]
                                        :url core/url?
                                        :age [core/between? 18 40]
                                        [:nested :value] [core/between? 1 10]])
            result (core/validate v-set {:email "test.email@googlemail.com"
                                         :post-code "B11 2SB"
                                         :matches "amatch"
                                         :zip-code 96801
                                         :url "http://google.com"
                                         :age 21
                                         :nested {:value 5}})]
        (is (:valid? result))
        (is (empty? (:results result)))))))
