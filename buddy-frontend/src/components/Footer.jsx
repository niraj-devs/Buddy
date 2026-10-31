
import React from "react";
import { Link } from "react-router-dom";
import "./Footer.css";

const links = [
  { label: "Features", href: "/#features" },
  { label: "How It Works", href: "/#how-it-works" },
  { label: "Explore Buddy", href: "/explore" },
];

export default function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="pf" id="footer">
      <div className="pf__glow" aria-hidden="true"></div>

      <div className="pf__inner">
        <div className="pf__top">
          {/* Brand */}
          <div className="pf__brand">
            <Link to="/" className="pf__logo" aria-label="Buddy home">
              Buddy
            </Link>

            <p className="pf__tagline">
              Your companion for college academics, career-ready skills, and
              opportunities that move you forward.
            </p>
          </div>

          {/* Navigation */}
          <nav className="pf__nav" aria-label="Footer navigation">
            <h3 className="pf__heading">Explore Buddy</h3>

            <ul>
              {links.map((item) => (
                <li key={item.label}>
                  {item.href.startsWith("/#") ? (
                    <a href={item.href}>{item.label}</a>
                  ) : (
                    <Link to={item.href}>{item.label}</Link>
                  )}
                </li>
              ))}
            </ul>
          </nav>

          {/* Waving Student */}
          <div className="pf__student">
            <div className="pf__student-glow"></div>

            <img
              src="/public/buddy-student-wave.png"
              alt="A student waving hello"
              className="pf__student-image"
              loading="lazy"
            />

            <span className="pf__wave-line pf__wave-line-one"></span>
            <span className="pf__wave-line pf__wave-line-two"></span>
            <span className="pf__wave-line pf__wave-line-three"></span>
          </div>
        </div>

        {/* Centered Status */}
        <div className="pf__journey">
          <span className="pf__status-dot" aria-hidden="true"></span>

          <p>Your journey. Your pace. Your future.</p>
        </div>

        {/* Centered Copyright */}
        <div className="pf__bottom">
          <p>
            © {currentYear} <strong>Buddy</strong>
            <span className="pf__love">
              Built with <span aria-label="love">♥</span> for students who
              want to move forward.
            </span>
          </p>

          <a href="#root" className="pf__back-top">
            Back to top <span aria-hidden="true">↑</span>
          </a>
        </div>
      </div>
    </footer>
  );
}
