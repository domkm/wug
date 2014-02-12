(ns domkm.wug.markov
  (:require [clojure.string :as str]))

(def ^:private words
  (->> (slurp "/usr/share/dict/words")
       str/split-lines
       (filter #(= % (str/lower-case %)))
       set))
