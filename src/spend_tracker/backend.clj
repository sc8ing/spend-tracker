(ns spend-tracker.backend 
  (:require
   [clojure.string :as string]))

(defn record-transaction [amount category]
  (println "recording transaction: " amount category))

(defn get-matching-categories
  "Find existing categories based on the input so far"
  [input]
  (let [existing-cats ["food" "lodging" "travel" "foraging"]]
    (for [cat existing-cats
          :when (and (string/starts-with? cat input) (not (string/blank? input)))]
      cat)))
