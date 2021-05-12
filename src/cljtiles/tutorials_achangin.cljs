(ns cljtiles.tutorials-achangin
  (:require [cljtiles.components :as cmpnts]))

(def content
  {:chapnames ["Lyrics"]
   :chaps [13] #_(count (:tutorials content))
   :tutorials
   [{:code ["Come gather 'round people"]
     :no-code-display true
     :comp-fn (fn [{:keys [result-raw]}]
                (cmpnts/lyric-format [result-raw] -472680540))}
    {:code ['(:tiles/vert (printlns "Wherever you roam"
                                    "And admit that the waters"))]
     :no-code-display true
     :comp-fn (fn [{:keys [stdout]}]
                (cmpnts/lyric-format stdout 1698792518))}
    {:blockpos [[0 0] [0 100] [0 150]]
     :code ['(:tiles/vert (printlns :tiles/slot
                                    "And accept it that soon"
                                    :tiles/slot))
            "Around you have grown"
            "You'll be drenched to the bone"]
     :no-code-display true
     :comp-fn (fn [{:keys [stdout]}]
                (cmpnts/lyric-format stdout -1282138944))}
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
                (cmpnts/lyric-format (conj stdout result-raw) 1352137510))}
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
                (cmpnts/lyric-format stdout 331859347))}
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
                (cmpnts/lyric-format (conj stdout result-raw) 1352137510))}
    {:blockpos [[0 0] [0 50] [0 100]]
     :code ['(str :tiles/slot :tiles/slot)
            "For the wheel's still in sp"
            "in"]
     :comp-fn (fn [{:keys [result-raw]}]
                (cmpnts/lyric-format [result-raw] -1697619396))}
    {:blockpos [[0 0] [0 50] [0 100] [0 150] [0 200] [0 250]]
     :code ['(str :tiles/slot :tiles/slot :tiles/slot :tiles/slot)
            "For the times they are"
            " "
            "a-chang"
            "in'"]
     :comp-fn (fn [{:keys [result-raw]}]
                (cmpnts/lyric-format [result-raw] -1399556977))}
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
            "won't"
            ''speak
            ]
     :comp-fn (fn [{:keys [result-raw]}]
                (cmpnts/lyric-from-vec result-raw 150106325))}
    {:blockpos [[0 0] [0 200]
                [0 300] [150 300] [250 300] [350 300] [450 300] [550 300]]
     :code ['(:tiles/vert
              [[:tiles/slot "there’s no tellin’ who"]
               ["that it’s" :tiles/slot]
               ["For the loser" :tiles/slot]
               ["will be" :tiles/slot "to win"]
               :tiles/slot])
            ["For the" :tiles/slot "they are a-changin’"]
            "namin'"
            ''now
            ''And
            ''later
            ''times]
     :comp-fn (fn [{:keys [result-raw]}]
                (cmpnts/lyric-from-vec result-raw -237908802))}
    {:blockpos [[0 0] [0 100] [0 200]]
     :code [:div>verse-rotate
            '(:tiles/vert [:tiles/slot
                           "For the wheel's still in spin"
                           "For the times they are a-changin’"])]}
    {:blockpos [[0 0] [0 100] [0 200]]
     :code [:div>verse-fade-in
            '(:tiles/vert [:tiles/slot
                           "The line it is drawn"
                           "The curse it is cast"
                           "The slow one now"
                           "Will later be fast"
                           "As the present now"
                           ])]}
    {:blockpos [[0 0] [0 100] [0 200]]
     :code [:div>verse-fade-out
            '(:tiles/vert [:tiles/slot
                           "Will later be past"
                           "The order is rapidly fadin'"
                           "And the first one now"
                           "Will later be last"
                           "For the times they are a-changin’"])]}]})
