(ns domkm.wug.markov
  (:refer-clojure :exclude [< > <= >= = not=])
  (:require [clojure.core :as clj]
            [clojure.string :as str]))

(def ^:private words
  (->> (slurp "resources/words")
       str/split-lines
       (filter #(clj/= % (str/lower-case %)))
       set))

(defn ^:private n-grams
  ([s] (mapcat #(n-grams % s) (range 1 (inc (count s)))))
  ([n s] (map #(apply str %)
              (partition n 1 s))))

(def ^:private n-gram-frequencies
  (frequencies (mapcat n-grams words)))

(def ^:private n-gram-sums
  (reduce-kv (fn [coll k v]
               (let [n (count k)]
                 (assoc coll n (+ v (coll n 0)))))
             {}
             n-gram-frequencies))

(defn ^:private n-gram-probability [n-gram]
  (/ (n-gram-frequencies n-gram 0)
     (n-gram-sums (count n-gram))))

(defn ^:private score [word]
  (->> (n-grams word)
       ; TODO: What is the optimal length-based scaling for n-grams?
       ; This scales by twice the length of the n-gram.
       (map #(* 2 (count %) (n-gram-probability %)))
       (reduce *)))

(defn ^:private def-comparator-fn [sym]
  `(defn ~sym [& args#]
     (apply ~(symbol "clojure.core" (name sym))
            (map score args#))))

(defmacro ^:private def-comparator-fns []
  `(do
     ~@(map def-comparator-fn '[< > <= >= = not=])))

(def-comparator-fns)
