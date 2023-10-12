(ns spend-tracker.htmx
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [hiccup2.core :as h]
            [clojure.string :as string]))

(defn- matching-categories
  "Find existing categories based on the input so far"
  [input]
  (let [existing-cats ["food" "lodging" "travel" "foraging"]]
    (for [cat existing-cats
          :when (and (string/starts-with? cat input) (not (string/blank? input)))]
      cat)))

(defn possible-categories [{:keys [category]}]
  (h/html
    [:ul (map (fn [i] (h/html [:li i])) (matching-categories category))]))

(defn in-page [content &{:keys [title] :or {title "Spend Tracker"}}]
  (h/html
    [:head
      [:title "Spend Tracker"]
      [:link {:rel "stylesheet" :href "/css/style.css"}]
      [:script {:src "/js/htmx.min.js"}]]
    [:body
     [:h1 title]
     content]))

(def record-txn-input
  (h/html
   [:div [:form {:method "post"
                 :action "rest/transaction"
                 :hx-post "rest/transaction"}
          [:input {:type "number"
                   :name "amount"}]
          [:div
           [:input {:type "text"
                    :name "category"
                    :hx-get "htmx/category"
                    :hx-target "#possible-categories"
                    :hx-trigger "keyup changed delay:500ms"}]
           [:div#possible-categories]]
          [:input {:type "submit"
                   :value "Add"}]]]))

(defn root-page [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (-> record-txn-input
             (in-page {:title "Record Transaction"})
             str)})

(compojure/defroutes routes
  (compojure/GET "/category" {params :params} (str (possible-categories params)))
  (route/not-found "Not Found"))

