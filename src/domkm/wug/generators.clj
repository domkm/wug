(ns domkm.wug.generators
  (:require [clojure.string :as str]
            [clojure.math.combinatorics :as combo]))

(def ^:private a-z "abcdefghijklmnopqrstuvwxyz")

(defn permutations
  ([s] (permutations s (count s)))
  ([s n] (->> (combo/combinations s n)
              distinct
              (mapcat combo/permutations)
              (map str/join))))
