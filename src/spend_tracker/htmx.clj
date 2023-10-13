(ns spend-tracker.htmx
  (:require
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

(def record-txn-input
  (h/html
   [:div [:form {:method "post"
                 :action "htmx/transaction"
                 :hx-post "htmx/transaction"}
          [:input {:type "number"
                   :name "amount"}]
          [:div
           [:input {:type "text"
                    :name "category"
                    :hx-get "htmx/matching-categories"
                    :hx-target "#matching-categories"
                    :hx-trigger "keyup changed delay:500ms"}]
           [:div#matching-categories.completion]]
          [:input {:type "submit"
                   :value "Add"}]]]))

(defn root-page [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (-> record-txn-input
             (wrap-in-main-page {:title "Record Transaction"})
             str)})

(defn matching-categories [{:keys [category]}]
  (h/html
    [:ul (map (fn [i] (h/html [:li i]))
              (backend/get-matching-categories category))]))

(compojure/defroutes routes
  (compojure/GET "/matching-categories" {params :params} (str (matching-categories params)))
  (route/not-found "Not Found"))

