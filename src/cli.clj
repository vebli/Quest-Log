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

(defn- gen-col-spec [table]
  (->> table
      (db/column-metadata)
      (map (fn [x] [(:name x) (sql-type->malli-type (:type x))]))
      (into [:map])
      ))

(println (gen-col-spec "habits"))

(defn- parse-cli-opts [table opts]
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
                 (update state :buffer conj word)))

   (coerce-cols [{:keys [cols] :as parsed-opts}]
                (let [coerced-cols (m/decode (gen-col-spec table) cols mt/string-transformer)]
                  (assoc parsed-opts :cols coerced-cols)) )))


(defn- table-alias->table [alias]
  (let [alias (keyword alias)
        m {:habit :habits
           :expense :expenses
           :task :tasks}]
    (get m alias alias))) 

(defn parse-cli-args [[cmd table-or-alias & opts]]
  (let [ table (table-alias->table table-or-alias)
        cmd (keyword cmd)
        {:keys [flags cols]} (parse-cli-opts table opts)
        request {:cmd cmd :table table :cols cols :opts flags }]
    request))


(defn- sql-type->malli-type [type]
  (let [m {:INTEGER :int
           :TEXT :string
           :DATE :inst}]
    (get m type type)))



(parse-cli-args ["add" "habit" "--name" "my-habit" "--description" "read"])
