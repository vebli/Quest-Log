(ns decode
  (:require [db :as db]
            [util :as util]
            [malli.core :as m]
            [clojure.string :as str]
            [malli.transform :as mt]
            [malli.dev.pretty :as pretty]))

(def request-spec
  [:map
   [:cmd :keyword]
   [:table :keyword]
   [:cols [:map-of :keyword :string]]
   [:opts [:map-of :keyword :string]]
   ])

(defn parse-cli-args [[cmd table-or-alias & opts]]
  (let [ table (table-alias->table table-or-alias)
        {:keys [flags cols]} (parse-opts table opts)]
    {:cmd cmd
     :table table
     :cols cols
     :opts flags }))

(defn- parse-opts [table opts]
  (util/wherefn 
        (-> (reduce #(parse-word table %1 %2)
                {:prev-opt nil
                 :flags {}
                 :cols {}
                 :buffer []} opts)
        (flush-option table)
        (select-keys [:flags :cols]))

   (flush-option [{:keys [prev-opt buffer] :as state} table]
                 (if prev-opt
                   (if (db/has-column? table prev-opt)
                     (-> state
                         (assoc-in [:cols prev-opt] buffer)
                         (assoc :buffer (empty buffer)))
                     (-> state
                         (assoc-in [:flags prev-opt] buffer)
                         (assoc :buffer (empty buffer))))
                   state))
   (start-option [table state option]
                 (-> state
                     (flush-option table)
                     (assoc :prev-opt option)))
   
   (parse-word [table state word]
               (if (str/starts-with? word "--")
                 (start-option table state (keyword (subs word 2)))
                 (update state :buffer conj word)))))

(defn- table-alias->table [alias]
  (let [alias (keyword alias)
        m {:habit :habits
           :expense :expenses
           :task :tasks}]
    (get m alias alias))) 

(defn- sql-type->malli-type [type]
  (let [m {"INTEGER" :int
           "TEXT" :string
           "DATE" :time/instant}]
    (get m type type)))

