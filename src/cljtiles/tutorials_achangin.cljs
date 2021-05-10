(ns cljtiles.tutorials-achangin)

(def content
  {:chapnames ["Lyrics"]
   :chaps [2] #_(count (:tutorials content))
   :tutorials
   [{:code ["Come gather 'round people"]}
    {:code ['(:tiles/vert (printlns "Come gather 'round people"
                                    "Wherever you roam"))]
     :comp-fn (fn [result-raw] [:p result-raw])}
    ]})
