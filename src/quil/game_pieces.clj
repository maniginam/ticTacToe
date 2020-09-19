(ns quil.game-pieces
  (:require [ttt.core :refer :all]
            [quil.core :as q]
            [quil.gui-game :refer :all]
            [quil.dimensions :as dim]))

(defn draw-X [center-x center-y]
  (let [half-height (/ dim/board-size 14)]
    (q/stroke-weight 20)
    (q/line (- center-x half-height) (- center-y half-height) (+ center-x half-height) (+ center-y half-height))
    (q/line (+ center-x half-height) (- center-y half-height) (- center-x half-height) (+ center-y half-height))))

(defn draw-O [center-x center-y]
  (let [radius (/ dim/board-size 14)
        weight 15]
    (q/stroke-weight weight)
    (q/no-fill)
    (q/ellipse-mode :center)
    (q/ellipse center-x center-y (+ (* 2 weight) (* radius 2)) (+ (* 2 weight) (* radius 2)))))

(defmethod draw-piece :player1 [state x y]
  (draw-X x y))

(defmethod draw-piece :player2 [state x y]
  (draw-O x y))







