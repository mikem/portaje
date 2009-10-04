(ns portaje
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
