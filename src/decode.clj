(ns decode
  (:require [db :as db]
            [schema.db :as schema]
            [util :as util]
            [malli.core :as m]
            [clojure.string :as str]
            [malli.transform :as mt]))

(def request-spec
  [:map
   [:cmd :keyword]
   [:table :keyword]
   [:cols [:map-of :keyword :string]]
   [:opts [:map-of :keyword :string]]
   ])

(defn- coerce-cols [table cols]
  (m/decode (schema/col-base table) cols mt/string-transformer))

(defn- parse-cli-opts [table opts]
  (util/wherefn 
   (-> (reduce #(parse-word table %1 %2)
               {:prev-opt nil
                :flags {}
                :cols {}
                :buffer []} opts)
       (flush-option table)
       (select-keys [:flags :cols])
       (into {:table table})
       (update :cols #(coerce-cols table %)))
   [(flush-option [{:keys [prev-opt buffer] :as state} table]
                  (let [is-col (db/has-column? table prev-opt)
                        key (if is-col :cols :flags)
                        opt-value (if (and (not is-col) (empty? buffer))
                                 true
                                 (first buffer))]
                    (if prev-opt
                      (-> state
                          (assoc-in [key prev-opt] opt-value)
                          (assoc :buffer (empty buffer))
                          ;; remove 'first' to allow for vector of values
                          ;; Maybe concat for strings?
                          )
                      state)))

    (start-option [table state option]
                  (-> state
                      (flush-option table)
                      (assoc :prev-opt option)))
    
    (parse-word [table state word]
                (if (str/starts-with? word "--")
                  (start-option table state (keyword (subs word 2)))
                  (update state :buffer conj word)))]))

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

(comment
 (parse-cli-args ["add" "habit" "--name" "my-habit" "--description" "read" "--target_count" 4 "--flag"]))


