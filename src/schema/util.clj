(ns schema.util)

(def local-date [:fn #(instance? java.time.LocalDate %)])

(def local-date-time [:fn #(instance? java.time.LocalDateTime %)])

(defn enum-range [& args]
  (into [:enum] (apply range args)))

