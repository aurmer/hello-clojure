(ns aurmer.hello-cljs.core
  (:require
    [clojure.string :as str]
    [re-frame.core :as rf]
    [reagent.core :as reagent]
    [ajax.core :as ajax]))

;; -----------------------------------------------------------------------------
;; Util

(defn parse-int [my-str]
  (if (js/isNaN (js/parseInt my-str))
      false
      (js/parseInt my-str)))

;; -----------------------------------------------------------------------------
;; App Db

(def initial-add-form
  {:x-value ""
   :y-value ""
   :sum-value ""
   :loading? false})

(def initial-time-form
  {:time-value nil
   :loading? false})

(def initial-db-state
  {:time-form initial-time-form
   :add-form initial-add-form})

;; -----------------------------------------------------------------------------
;; Events

(rf/reg-event-db
  :init
  (fn [_ _]
    initial-db-state))

(rf/reg-event-db
  :update-time
  (fn [db [_ time-str]]
    (update db :time-form assoc :time-value time-str :loading? false)))

(rf/reg-event-db
  :submit-time-form
  (fn [db _]
    (update db :time-form assoc :time-value nil :loading? true)))


(rf/reg-event-db
  :update-add-value
  (fn [db [_ new-form]]
    (def parsed-int (parse-int (-> new-form first val)))
    (def my-keyword (-> new-form first key name keyword))
    (if parsed-int
        (update db :add-form assoc my-keyword parsed-int :sum-value "")
        (update db :add-form assoc my-keyword "" :sum-value ""))))

(rf/reg-event-db
  :update-sum-value
  (fn [db [_ sum]]
    (update db :add-form assoc :sum-value sum :loading? false)))

(rf/reg-event-db
  :submit-add-form
  (fn [db _]
    (update db :add-form assoc :loading? true)))

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  :time-form
  (fn [db _]
    (:time-form db)))

(rf/reg-sub
  :add-form
  (fn [db _]
    (:add-form db)))

;; -----------------------------------------------------------------------------
;; Views

(defn TimeForm []
  (let [form @(rf/subscribe [:time-form])
        {:keys [time-value loading?]} form]
    [:div.content.flex-aligned
     [:button.button.is-info.spaced
       {:class (if loading? "is-loading" "")
        :on-click (fn [e]
                    (rf/dispatch [:submit-time-form])
                    (js/setTimeout ;1 sec timeout is just for fun to display the loading state
                      (fn []
                        (ajax/GET "https://aub.re/api/time"
                          {:handler (fn [res] (rf/dispatch [:update-time (-> res (get "response") (get "localtimestamp"))]))
                           :error-handler (fn [err] (. js/console (log err)))}))
                      1000))}
      "Get Time"]
     [:span {:id "time"}] time-value]))


(defn AddForm []
  (let [form @(rf/subscribe [:add-form])
        {:keys [x-value y-value sum-value loading?]} form]
    [:div.content.flex-aligned
       [:input.small.spaced
          {:id "x_val"
           :on-change (fn [e]
                        (.preventDefault e)
                        (rf/dispatch [:update-add-value {:x-value (.. e -currentTarget -value)}]))
           :value x-value}]
       "+"
       [:input.small.spaced
          {:id "y_val"
           :on-change (fn [e]
                        (.preventDefault e)
                        (rf/dispatch [:update-add-value {:y-value (.. e -currentTarget -value)}]))
           :value y-value}]
       [:button.button.is-info
          {:class (if loading? "is-loading" "")
           :on-click (fn [e]
                       (rf/dispatch [:submit-add-form])
                       (js/setTimeout ;1 sec timeout is just for fun to display the loading state
                         (fn []
                           (ajax/GET (str "https://aub.re/api/sum-2-int/" x-value "/" y-value)
                             {:handler (fn [res] (rf/dispatch [:update-sum-value (-> res (get "response") (get "sum"))]))
                              :error-handler (fn [err] (rf/dispatch [:update-sum-value ""]))}))
                         1000))}
          "=>"]
       [:input.small.spaced
        {:id "sum"
         :readOnly true
         :value sum-value}]]))


(defn App []
    [:section.section
      [:h1.title "Hello "
                 [:img.tooling-logo
                  {:src "https://raw.githubusercontent.com/cljs/logo/master/cljs.svg"}]
                 " and "
                 [:img.tooling-logo
                  {:src "https://raw.githubusercontent.com/day8/re-frame/master/docs/images/logo/re-frame-colour.png"}]]
      [TimeForm]
      [AddForm]])



;; -----------------------------------------------------------------------------
;; Init

(def app-container-el (js/document.getElementById "root"))

(defn re-render
  "Forces a Reagent re-render of all components.
   NOTE: this function is called after every shadow-cljs hot module reload"
  []
  (reagent/force-update-all))

(def init!
  (fn []
    (rf/dispatch-sync [:init])
    (when-not (str/blank? (:master-password initial-db-state))
      (rf/dispatch [:submit-master-password]))
    (reagent/render [App] app-container-el)))

(init!)
