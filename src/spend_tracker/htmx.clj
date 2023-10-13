(ns spend-tracker.htmx
  (:require
   [clojure.data.json :as json]
   [compojure.core :as compojure]
   [compojure.route :as route]
   [hiccup2.core :as h]
   [spend-tracker.backend :as backend]))

(defn wrap-in-main-page [content &{:keys [title] :or {title "Spend Tracker"}}]
  (h/html
    [:head
      [:title "Spend Tracker"]
      [:link {:rel "stylesheet" :href "/css/style.css"}]
      [:script {:src "/js/htmx.min.js"}]]
    [:body
     [:h1 title]
     content]))

(defn record-txn-input [prefilled]
  (println "prefilled was" prefilled)
  (h/html
   [:div#record-txn-input [:form {:method "post"
                                  :action "htmx/transaction"
                                  :hx-post "htmx/transaction"}
                           [:input {:type "number"
                                    :name "amount"
                                    :value (or (:amount prefilled) 0)}]
                           [:div
                            [:input {:type "text"
                                     :name "category"
                                     :value (or (:category prefilled) "")
                                     :hx-get "htmx/matching-categories"
                                     :hx-target "#matching-categories"
                                     :hx-trigger "keyup changed delay:500ms"}]
                            [:div#matching-categories.completion]]
                           [:input {:type "submit"
                                    :value "Add"}]]]))


(defn root-page [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (-> (record-txn-input {})
             (wrap-in-main-page {:title "Record Transaction"})
             str)})

(defn matching-categories [{:keys [category]}]
  (h/html
    [:ul (map (fn [i] (h/html [:li {:hx-target "#record-txn-input"
                                    :hx-get "htmx/record-txn-input"
                                    :hx-vals (json/write-str {:category i})}
                               i]))
              (backend/get-matching-categories category))]))

(defn handle-record-transaction [{:keys [amount category]}]
  (backend/record-transaction amount category)
  (str (record-txn-input {})))

(compojure/defroutes routes
  (compojure/GET "/matching-categories" {params :params} (str (matching-categories params)))
  (compojure/GET "/record-txn-input" {params :params} (str (record-txn-input params)))
  (compojure/POST "/transaction" {params :params} (handle-record-transaction params))
  (route/not-found "Not Found"))

