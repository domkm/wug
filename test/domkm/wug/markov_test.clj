(ns domkm.wug.markov-test
  (:require [clojure.test :as test :refer [is deftest with-test run-tests testing]]
            [clojure.string :as str]
            [clojure.java.shell :refer [sh]]
            [clojure.math.combinatorics :as combo]
            [domkm.wug.markov :as markov]))

(defn log [words]
  (spit (str "logs/" (System/currentTimeMillis))
        (str/join "\n" (map str/join words))))

(defn rm-logs [] (sh "sh" "-c" "rm logs/*"))

(def a-z "abcdefghijklmnopqrstuvwxyz")

(def strings (take-nth 4000 (combo/selections a-z 5)))
