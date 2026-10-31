import React from "react";
import "./BookAnimation.css";

export default function BookAnimation() {
  return (
    <div className="buddy-book-scene" aria-hidden="true">
      <div className="buddy-book-glow" />
      <div className="buddy-book">
        <div className="buddy-book-cover" />
        <div className="buddy-book-pages">
          {Array.from({ length: 8 }, (_, i) => (
            <div
              key={i}
              className="buddy-book-page"
              style={{
                animationDelay: `${i * 0.65}s`
              }}
            />
          ))}
        </div>
        <div className="buddy-book-spine" />
      </div>
      <div className="buddy-book-shadow" />
    </div>
  );
}
