(ns schema.recur
  (:require [schema.util :as s-util]
            [malli.core :as m]))

(def periodic
  (let [periods [:enum :daily :weekly :monthly :yearly]]
    [:map
     [:freq periods]
     [:start s-util/inst]
     [:end s-util/inst]
     [:interval :int] 
     [:days [:vector :int]]
     ]))
