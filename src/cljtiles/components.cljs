(ns cljtiles.components
  (:require [goog.string :as gstring]))

(defn html-verse-rotate-comp [& e]
  (into [:div.rotate] (map #(vector :p %) e)))

(defn make-text [text css-class animation-delay anim-offset]
  (let [f (fn [i x]
            [:span {:class css-class
                    :style {:animationDelay (str (+ anim-offset (* i animation-delay)) "ms")}}
             (if (= x " ") (gstring/unescapeEntities "&nbsp;") x)])]
    (into [:p] (map-indexed f text))))

(defn offsets [textvec]
  (cons 0 (reductions + (map count textvec))))

(defn lyric-fade-in-sub [textvec]
  (fn [] (into [:<>] (map (fn [x os] (make-text x "char-in" 6 (* os 6))) textvec (offsets textvec)))))

(defn html-verse-fade-in-comp [& textvec]
  (lyric-fade-in-sub textvec))

(defn lyric-format [textvec hash-code]
  #_(println "thehash " (hash textvec))
  (when (= (hash textvec) hash-code)
    (lyric-fade-in-sub textvec))) ;; concious decision for () instead of []

(defn lyric-from-vec [vectextvec hash-code]
  #_(println "thehash " (hash vectextvec))
  (when (= (hash vectextvec) hash-code)
    (let [textvec (map #(apply str (interpose " " %)) vectextvec)]
      (lyric-fade-in-sub textvec)))) ;; concious decision for () instead of []

(defn lyric-fade-out-sub [textvec]
  (fn [] (into [:<>] (map (fn [x os] (make-text x "char-out" 6 (* os 6))) textvec (offsets textvec)))))

(defn html-verse-fade-out-comp [& textvec]
  (lyric-fade-out-sub textvec)) ;; concious decision for () instead of []
