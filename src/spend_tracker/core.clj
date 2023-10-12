(ns spend-tracker.core
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [hiccup2.core :as h]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]])
  (:gen-class))

;; Todo: make htmx module separate from rest module, isolate routes

(defn in-page [content &{:keys [title] :or {title "Spend Tracker"}}]
  (h/html
    [:head
      [:title "Spend Tracker"]
      [:link {:rel "stylesheet" :href "/css/style.css"}]
      [:script {:src "/js/htmx.min.js"}]]
    [:body
     [:h1 title]
     content]))

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
                    :hx-get "/category"
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

(defn record-transaction [{:keys [amount category]}]
  (println "recording transaction: " amount category)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str record-txn-input)})

(compojure/defroutes app-routes
  (compojure/GET "/" _ root-page)
  (compojure/GET "/category" {params :params} (str (possible-categories params)))
  (compojure/POST "/rest/transaction" {params :params} (record-transaction params))
  (route/not-found "Not Found"))

(def app (-> #'app-routes
             (wrap-keyword-params)
             (wrap-params)
             (wrap-resource "public")))

(defn -main
  [& _]
  (run-jetty #'app {:port 3000
                    :join? false}))
