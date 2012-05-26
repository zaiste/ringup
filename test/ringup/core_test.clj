(ns ringup.core-test
  (:use clojure.test
        ringup.core))

(deftest a-test
  (testing "FIXME, I fail."
           (is (= 1 1))))

(deftest is-it-ok 
  (let [req {} 
        resp (handler req)]
    (is (= 200 (:status resp)))
    (is (= "Hello, Ring!" (:body resp)))))