(ns test.portaje
  (:require [clojure.contrib.str-utils2 :as s])
  (:use [clojure.test])
  (:use portaje))

(deftest test-parse-release
  (is (= ["3" ["1.0" "clojure"]]
         (parse-release ["r3" "1.0" "clojure"])))
  (is (= ["0" ["1.0" "contrib" "clojure"]]
         (parse-release ["r0" "1.0" "contrib" "clojure"])))
  (is (= ["0" ["1.0" "clojure"]]
         (parse-release ["1.0" "clojure"]))))

(deftest test-parse-version
  (is (= ["1.0" "clojure"]
         (parse-version ["1.0" "clojure"])))
  (is (= ["1.0_alpha4" "clojure"]
         (parse-version ["1.0_alpha4" "clojure"])))
  (is (= ["1.0.1" "clojure-contrib"]
         (parse-version ["1.0.1" "contrib" "clojure"]))))

(deftest test-parse-package-version
  (is (= ["clojure" "1.0" "0"]
         (parse-package-version "clojure-1.0")))
  (is (= ["clojure-contrib" "0.9.5" "8"]
         (parse-package-version "clojure-contrib-0.9.5-r8")))
  (is (= ["clojure" "1.1.0_beta" "3"]
         (parse-package-version "clojure-1.1.0_beta-r3")))
  (is (= ["mozilla-firefox" "3.5.2" "0"]
         (parse-package-version "mozilla-firefox-3.5.2"))))

(deftest test-split-category-and-package
  (is (= ["dev-lang" "clojure-1.0-r1"]
         (split-category-and-package "dev-lang/clojure-1.0-r1")))
  (is (= [nil "clojure-contrib-1.0"]
         (split-category-and-package "clojure-contrib-1.0"))))

(run-tests)
