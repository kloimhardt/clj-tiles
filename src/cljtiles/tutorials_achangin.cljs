(ns cljtiles.tutorials-achangin)

(def content
  {:chapnames ["Lyrics"]
   :chaps [2] #_(count (:tutorials content))
   :tutorials
   [{:code ["Come gather 'round people"]
     :comp-fn (fn [{:keys [result-raw]}]
                (when (= (hash result-raw) 1745298409)
                  (fn [] [:p result-raw])))}
    {:code ['(:tiles/vert (printlns "Come gather 'round people"
                                    "Wherever you roam"))]
     :comp-fn (fn [{:keys [stdout]}]
                (when (= (hash stdout) 1058487531)
                  (fn [] (into [:<>] (map #(vector :p %) stdout)))))}]})
