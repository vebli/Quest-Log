(ns decode
  (:require [db :as db]
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

(defn- sql-type->malli-type [type]
  (let [time [:fn #(instance? java.time.Instant %)]
        m {:INTEGER :int
           :TEXT :string
           :DATE time}]
    (get m type type)))


(defn- coerce-cols [{:keys [cols] :as parsed-opts}]
  (util/wherefn 
   (assoc parsed-opts :cols (coerce cols))

   [(gen-col-schema [table]
                    (->> table
                         (db/column-metadata)
                         (map (fn [x]
                                [(keyword (:name x))
                                 (sql-type->malli-type (keyword (:type x)))]))
                         (into [:map])
                         (m/schema)
                         ))
    (coerce [c] (m/decode (gen-col-schema table) c mt/string-transformer))]))

(println (gen-col-schema "habits"))

(defn- parse-cli-opts [table opts]
  (util/wherefn 
   (-> (reduce #(parse-word table %1 %2)
               {:prev-opt nil
                :flags {}
                :cols {}
                :buffer []} opts)
       (flush-option table)
       (select-keys [:flags :cols])
       (coerce-cols)
       )

   [(flush-option [{:keys [prev-opt buffer] :as state} table]
                  (let [key (if (db/has-column? table prev-opt) :cols :flags)]
                    (if prev-opt
                      (-> state
                          (assoc :buffer (empty buffer))
                          (assoc-in [key prev-opt] (first buffer))
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
                  (update state :buffer conj word)))]
   ))


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
  (defn coerce-cols [{:keys [cols table] :as parsed-opts}]
    (let [coerced-cols (m/decode (gen-col-schema table) cols mt/string-transformer)]
      (assoc parsed-opts :cols coerced-cols)))
 (coerce-cols (parse-cli-args ["add" "habit" "--name" "my-habit" "--description" "read" "--my-flag"]))
 (m/schema? (m/schema [:map [:id :int]]))
 (m/schema? (gen-col-schema "habits"))
 (def table "habits")
 (defn coerce-cols [{:keys [cols] :as parsed-opts}]
              (let [coerced-cols (m/decode (gen-col-schema table) cols mt/string-transformer)]
                (assoc parsed-opts :cols coerced-cols)))
 (gen-col-spec "habits")
 (parse-cli-args ["add" "habit" "--name" "my-habit" "--description" "read" "--target_count" 4]))

