(ns quil.dimensions)

(def console-size [700 800])
(def board-size 700)
(def button-top-left-corner [120 715])
(def button-size [460 70])
(def prompt-x (* (/ board-size 2) 0.4))
(def prompt-y 235)
(def prompt-size [(* board-size 0.6) (/ board-size 3)])

(defn option-dimensions [option]
  {:width (* (first prompt-size) 0.6)
   :height 30
   :left (+ prompt-x (* (first prompt-size) 0.2))
   :top (+ (* prompt-y 1.3) (* option 50))})

(defn piece-option-dimensions [option]
  {:left (+ prompt-x (* (/ (first prompt-size) 5) option) (* (/ (first prompt-size) 5) (dec option)))
   :top (+ prompt-y (/ (second prompt-size) 3))
   :width 100
   :height 100})

(def board-size-entry
  {:left (- (+ prompt-x (/ (first prompt-size) 2)) 25)
   :top (- (+ prompt-y (/ (second prompt-size) 2)) 25)
   :width 100})