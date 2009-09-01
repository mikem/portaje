(ns portaje
  (:use [clojure.test])
  (:import [java.io File])
  (:require [clojure.contrib.str-utils2 :as s])
  (:use [clojure.contrib.duck-streams :only (read-lines)]))

(defn parse-release [release]
  "Returns the number of this release and (rest release)"
  (let [matches (re-matches #"^r(\d+)" (first release))]
    (if (= nil matches)
      ["0" release]
      [(last matches) (rest release)])))

(defn parse-version [version]
  "Returns the version and (rest version)"
  [(first version) (s/join "-" (reverse (rest version)))])

(defn parse-package-version [package-spec]
  "Returns the package name, version and release"
  (let [reverse-package-list (reverse (s/split package-spec #"-"))
        [release reverse-package-list] (parse-release reverse-package-list)
        [version package-name] (parse-version reverse-package-list)]
    [package-name version release]))

(defn split-category-and-package [package-spec]
  "Returns the category (or nil if not specified) and package as strings"
  (let [package-split (s/split package-spec #"/")]
    (if (= 2 (count package-split))
      package-split
      (cons nil package-split))))

(defn parse-package-spec [package-spec]
  "Returns a map describing the package"
  (let [[category package] (split-category-and-package package-spec)
        [package version release] (parse-package-version package)]
    {:category category,
     :name package,
     :version version,
     :release release}))

(defn get-package-specs [keyword-dir]
  "Returns a lazy sequence of files found in keyword-dir"
  (let [file-list (filter #(.isFile %) (file-seq (File. keyword-dir)))]
    (map #(read-lines %) file-list)))

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
