(ns schema.db
  (:require [db :as db]
            [schema.util :as s-util]
            [malli.core :as m]))

(defn- sql-type->malli-type [type]
  (let [m {:INTEGER :int
           :TEXT :string
           :DATE s-util/local-date-time}]
    (get m type type)))

(defn col-base [table]
 (->> table
      (db/column-metadata)
      (map (fn [{:keys [name type notnull dflt_value pk]}]
             [(keyword name)
              {
               :optional true ;; everything optional by default 
               ;; metadata (not used by malli directly)
               :notnull (or (not (zero? notnull)) (= pk 1))
               :default dflt_value} ;TODO coercion of dflt value
              (sql-type->malli-type (keyword type))]))
      (into [:map])))

(defn col-default [[_ & cols] col]
  (some (fn [[curr-col {:keys [default]}]]
          (if (= curr-col col) default ))
        cols))

(defn col-default? [schema col]
  (not (nil? (col-default schema col))))

(defn col-nullable? [[_ & cols] col]
  (boolean (some (fn [[curr-col {:keys [notnull]}]]
          (and (= curr-col col) (not notnull)))
  cols)))


(defn require-cols [[k & cols] required-cols]
  (into [k] (map
             (fn [[curr-col opts type :as col]]
               (if (contains? required-cols curr-col)
                 (assoc-in col [1 :optional] false )
                 col))
             cols)))

(defn insertion [[k & cols]]
  "Takes col-schema and sets :optional to false 
for columns required for insertion"
  (into [k]
        (map
         (fn [[curr-col {:keys [notnull default]} type :as col]]
           (if (and notnull (not default))
             (assoc-in col [1 :optional] false))
           col))
        cols))

