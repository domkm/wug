(ns domkm.wug.markov
  (:refer-clojure :exclude [< > <= >= = not=])
  (:require [clojure.core :as clj]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def ^:private words
  (-> "words.txt"
      io/resource
      slurp
      str/split-lines))

(defn ^:private n-grams
  ([s] (mapcat #(n-grams % s) (range 1 (inc (count s)))))
  ([n s] (map str/join
              (partition n 1 s))))

(defn ^:private n-grams-fixes [s]
  (reduce (fn [-fixes n]
            (let [ng (n-grams n s)]
              {:pre (conj (:pre -fixes []) (first ng))
               :post (conj (:post -fixes []) (last ng))
               :in (into (:in -fixes []) (butlast (rest ng)))}))
          {}
          (range 2 5)))

(def ^:private n-gram-frequencies
  (reduce-kv #(assoc %1 %2 (frequencies %3))
             {}
             (reduce #(merge-with into %1 %2)
                     (mapv n-grams-fixes words))))

(def ^:private n-gram-sums
  (let [sum-freqs (fn [freqs]
                    (reduce-kv (fn [m k v]
                                 (let [n (count k)]
                                   (assoc m n (+ v (m n 0)))))
                               {}
                               freqs))]
    (reduce-kv (fn [m k v] (assoc m k (sum-freqs v)))
               {}
               n-gram-frequencies)))

(defn ^:private n-gram-probability [n-gram -fix]
  (/ (get-in n-gram-frequencies [-fix n-gram] 0)
     (get-in n-gram-sums [-fix (count n-gram)])))

(defn ^:private score* [[-fix n-grams]]
  (* (reduce *
             ; TODO: What is the optimal length-based scaling for n-grams?
             ; This scales by the square of the length of the n-gram.
             (map #(* (count %) (count %) (n-gram-probability % -fix))
                  n-grams))
     (if (clj/= :in -fix)
         1
         1))) ; TODO: How much more important are prefixes and suffixes?
                   ; Also, this multiplier seems completely irrelevant. Why?

(defn score [word]
  (reduce * (map score* (n-grams-fixes word))))

(defn ^:private def-comparator-fn [sym]
  `(defn ~sym [& args#]
     (apply ~(symbol "clojure.core" (name sym))
            (map score args#))))

(defmacro ^:private def-comparator-fns []
  `(do
     ~@(map def-comparator-fn '[< > <= >= = not=])))

(def-comparator-fns)
