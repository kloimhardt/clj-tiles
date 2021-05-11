(ns cljtiles.tutorials-achangin
  (:require [goog.string :as gstring]))

(defn make-text [text css-class animation-delay anim-offset]
  (let [f (fn [i x]
            [:span {:class css-class
                    :style {:animationDelay (str (+ anim-offset (* i animation-delay)) "ms")}}
             (if (= x " ") (gstring/unescapeEntities "&nbsp;") x)])]
    (into [:p] (map-indexed f text))))

(defn offsets [textvec]
  (cons 0 (reductions + (map count textvec))))

(defn lyric-format-sub [textvec]
  (fn [] (into [:<>] (map (fn [x os] (make-text x "char-in" 6 (* os 6))) textvec (offsets textvec)))))

(defn lyric-format [textvec hash-code]
  (println "thehash " (hash textvec))
  (when (= (hash textvec) hash-code)
    (lyric-format-sub textvec)))

(defn lyric-from-vec [vectextvec hash-code]
  (println "thehash " (hash vectextvec))
  (when (= (hash vectextvec) hash-code)
    (let [textvec (map #(apply str (interpose " " %)) vectextvec)]
      (lyric-format-sub textvec))))

(def content
  {:chapnames ["Lyrics"]
   :chaps [10] #_(count (:tutorials content))
   :tutorials
   [{:code ["Come gather 'round people"]
     :no-code-display true
     :comp-fn (fn [{:keys [result-raw]}]
                (lyric-format [result-raw] -472680540))}
    {:code ['(:tiles/vert (printlns "Wherever you roam"
                                    "And admit that the waters"))]
     :no-code-display true
     :comp-fn (fn [{:keys [stdout]}]
                (lyric-format stdout 1698792518))}
    {:blockpos [[0 0] [0 100] [0 150]]
     :code ['(:tiles/vert (printlns :tiles/slot
                                    "And accept it that soon"
                                    :tiles/slot))
            "Around you have grown"
            "You'll be drenched to the bone"]
     :no-code-display true
     :comp-fn (fn [{:keys [stdout]}]
                (lyric-format stdout -1282138944))}
    {:blockpos [[0 0] [0 150] [0 200] [0 250]]
     :code ['(:tiles/vert (printlns "If your time to you"
                                    :tiles/slot
                                    "Then you better start swimmin'"
                                    :tiles/slot
                                    ))
            "For the times they are a-changin'"
            "Is worth savin'"
            "Or you'll sink like a stone"]
     :no-code-display true
     :comp-fn (fn [{:keys [stdout result-raw]}]
                (lyric-format (conj stdout result-raw) 1352137510))}
    {:blockpos [[0 0] [0   200] [0   250] [0   300]
                      [300 200] [300 250] [300 300]]
     :code ['(:tiles/vert (printlns :tiles/slot
                                    :tiles/slot
                                    :tiles/slot
                                    :tiles/slot
                                    :tiles/slot
                                    :tiles/slot))
            "You'll be drenched to the bone"
            "Wherever you roam"
            "Around you have grown"
            "And admit that the waters"
            "And accept it that soon"
            "Come gather 'round people"]
     :no-code-display true
     :comp-fn (fn [{:keys [stdout]}]
                (lyric-format stdout 331859347))}
    {:blockpos [[0 0] [0   200] [0   250] [0   300]
                [300 200] [300 250] [300 300]]
     :code ['(:tiles/vert (printlns :tiles/slot
                                    :tiles/slot
                                    :tiles/slot
                                    :tiles/slot))
            "Is worth savin'"
            "For the times they are a-changin'"
            "Or you'll sink like a stone"
            "If your time to you"
            "Then you better start swimmin'"]
     :no-code-display true
     :comp-fn (fn [{:keys [stdout result-raw]}]
                (lyric-format (conj stdout result-raw) 1352137510))}
    {:blockpos [[0 0] [0 50] [0 100]]
     :code ['(str :tiles/slot :tiles/slot)
            "For the wheel's still in sp"
            "in"]
     :comp-fn (fn [{:keys [result-raw]}]
                (lyric-format [result-raw] -1697619396))}
    {:blockpos [[0 0] [0 50] [0 100] [0 150] [0 200] [0 250]]
     :code ['(str :tiles/slot :tiles/slot :tiles/slot :tiles/slot)
            "For the times they are"
            " "
            "a-chang"
            "in'"]
     :comp-fn (fn [{:keys [result-raw]}]
                (lyric-format [result-raw] -1399556977))}
    {:blockpos [[0 0]
                [0 300] [100 300] [200 300] [300 300] [400 300] [500 300]]
     :code ['(:tiles/vert
              [["Come" :tiles/slot "and critics"]
               [:tiles/slot "prophesize with your pen"]
               ["And keep your" :tiles/slot "wide"]
               ["The chance" :tiles/slot "come again"]
               ["And don’t" :tiles/slot "too soon"]
               ["For the wheel’s still in" :tiles/slot]])
            ''writers
            ''Who
            ''spin
            ''eyes
            ''won't
            ''speak
            ]
     :comp-fn (fn [{:keys [result-raw]}]
                (lyric-from-vec result-raw -395749415))}
    {:blockpos [[0 0] [0 200]
                [0 300] [100 300] [200 300] [300 300] [400 300] [500 300]]
     :code ['(:tiles/vert
              [[:tiles/slot "there’s no tellin’ who"]
               ["that it’s" :tiles/slot]
               ["For the loser" :tiles/slot]
               ["will be" :tiles/slot "to win"]
               :tiles/slot])
            ["For the" :tiles/slot "they are a-changin’"]
            ''namin’
            ''now
            ''And
            ''later
            ''times]
     :comp-fn (fn [{:keys [result-raw]}]
                (lyric-from-vec result-raw 792318763))}]})
