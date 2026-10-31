import { useEffect, useRef } from 'react';
import './NeonGraduationBackground.css';

export function NeonGraduationBackground({
  particleCount = 95,
  connectionDistance = 155,
  animationSpeed = 1.0,
  capFloatRange = 14,
  glowIntensity = 1.15,
  interactiveMouse = true,
  isPaused = false,
  colorPreset = 'cyber-edu',
  showCap = true,
  className = ''
}) {
  const canvasRef = useRef(null);
  const mouseRef = useRef({ x: -9999, y: -9999, targetX: 0, targetY: 0, active: false });

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    let animationFrameId;
    let width = (canvas.width = canvas.parentElement?.clientWidth || window.innerWidth);
    let height = (canvas.height = canvas.parentElement?.clientHeight || window.innerHeight);

    const palettes = {
      'cyber-edu': {
        bgLeft: '#063445',
        bgMid: '#070b24',
        bgRight: '#24052e',
        cyan: '#00f2fe',
        cyanRgb: '0, 242, 254',
        magenta: '#ff0878',
        magentaRgb: '255, 8, 120',
        purple: '#a81ee6',
        purpleRgb: '168, 30, 230'
      },
      'electric-gold': {
        bgLeft: '#062c3a',
        bgMid: '#090d24',
        bgRight: '#2a1205',
        cyan: '#00e5ff',
        cyanRgb: '0, 229, 255',
        magenta: '#ffaa00',
        magentaRgb: '255, 170, 0',
        purple: '#ff4d00',
        purpleRgb: '255, 77, 0'
      },
      'violet-aurora': {
        bgLeft: '#073832',
        bgMid: '#0a0826',
        bgRight: '#280640',
        cyan: '#10f5c0',
        cyanRgb: '16, 245, 192',
        magenta: '#d916ff',
        magentaRgb: '217, 22, 255',
        purple: '#7928ca',
        purpleRgb: '121, 40, 202'
      }
    };

    const pal = palettes[colorPreset] || palettes['cyber-edu'];

    // Create plexus particles weighted toward the right side like the reference image
    const createParticles = (count, w, h) => {
      const list = [];
      for (let i = 0; i < count; i++) {
        // Bias ~68% of particles toward the right half where the graduation cap & dense plexus live
        const biasRight = Math.random() < 0.68;
        const x = biasRight
          ? w * (0.38 + Math.random() * 0.62)
          : w * (Math.random() * 0.45);
        const y = h * Math.random();
        const isMagentaZone = x > w * 0.56 || (x > w * 0.42 && Math.random() > 0.45);

        list.push({
          x,
          y,
          vx: (Math.random() - 0.5) * 0.55,
          vy: (Math.random() - 0.5) * 0.55,
          radius: Math.random() < 0.16 ? 2.4 + Math.random() * 1.8 : 1.1 + Math.random() * 1.3,
          colorType: isMagentaZone ? (Math.random() > 0.3 ? 'magenta' : 'purple') : 'cyan',
          pulsePhase: Math.random() * Math.PI * 2,
          pulseSpeed: 0.02 + Math.random() * 0.03
        });
      }
      return list;
    };

    // Ambient cosmic dust speckles inside & around the cap
    const createDust = (w, h) => {
      const speckles = [];
      for (let i = 0; i < 65; i++) {
        speckles.push({
          offsetX: (Math.random() - 0.5) * 290,
          offsetY: (Math.random() - 0.5) * 180,
          size: 0.6 + Math.random() * 1.4,
          alpha: 0.25 + Math.random() * 0.75,
          phase: Math.random() * Math.PI * 2
        });
      }
      return speckles;
    };

    let particles = createParticles(particleCount, width, height);
    let dustSpeckles = createDust(width, height);

    const handleResize = () => {
      if (!canvas) return;
      width = canvas.width = canvas.parentElement?.clientWidth || window.innerWidth;
      height = canvas.height = canvas.parentElement?.clientHeight || window.innerHeight;
      particles = createParticles(particleCount, width, height);
    };

    const handleMouseMove = (e) => {
      if (!interactiveMouse) return;
      const rect = canvas.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      mouseRef.current.x = x;
      mouseRef.current.y = y;
      mouseRef.current.targetX = (x / width - 0.5) * 2;
      mouseRef.current.targetY = (y / height - 0.5) * 2;
      mouseRef.current.active = true;
    };

    const handleMouseLeave = () => {
      mouseRef.current.active = false;
      mouseRef.current.x = -9999;
      mouseRef.current.y = -9999;
      mouseRef.current.targetX = 0;
      mouseRef.current.targetY = 0;
    };

    window.addEventListener('resize', handleResize);
    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('mouseleave', handleMouseLeave);

    let time = 0;
    let parallaxX = 0;
    let parallaxY = 0;

    // Draw the 3D Neon Wireframe Graduation Cap matching the uploaded artwork
    const drawGraduationCap = (ctx, centerX, centerY, scale, t) => {
      ctx.save();

      // Gentle levitation + subtle tilt
      const floatY = Math.sin(t * 1.4) * capFloatRange;
      const floatX = Math.cos(t * 0.9) * (capFloatRange * 0.35);
      const tiltAngle = -0.19 + Math.sin(t * 1.1) * 0.025 + parallaxX * 0.04;

      ctx.translate(centerX + floatX + parallaxX * 18, centerY + floatY + parallaxY * 14);
      ctx.rotate(tiltAngle);
      ctx.scale(scale, scale);

      // 1. Intense Ambient Back-Glow behind the cap's top-right peak & cyan left rim
      const magentaBloom = ctx.createRadialGradient(45, -75, 5, 45, -65, 220);
      magentaBloom.addColorStop(0, `rgba(${pal.magentaRgb}, ${0.52 * glowIntensity})`);
      magentaBloom.addColorStop(0.45, `rgba(${pal.purpleRgb}, ${0.22 * glowIntensity})`);
      magentaBloom.addColorStop(1, 'rgba(0, 0, 0, 0)');
      ctx.fillStyle = magentaBloom;
      ctx.beginPath();
      ctx.arc(45, -65, 220, 0, Math.PI * 2);
      ctx.fill();

      const cyanBloom = ctx.createRadialGradient(-95, 48, 4, -85, 48, 155);
      cyanBloom.addColorStop(0, `rgba(${pal.cyanRgb}, ${0.46 * glowIntensity})`);
      cyanBloom.addColorStop(0.5, `rgba(${pal.cyanRgb}, ${0.14 * glowIntensity})`);
      cyanBloom.addColorStop(1, 'rgba(0, 0, 0, 0)');
      ctx.fillStyle = cyanBloom;
      ctx.beginPath();
      ctx.arc(-85, 48, 155, 0, Math.PI * 2);
      ctx.fill();

      // 2. Skullcap (the lower rounded base of the mortarboard)
      // Coordinates relative to cap center
      ctx.save();
      const skullGrad = ctx.createLinearGradient(-95, 25, 115, 95);
      skullGrad.addColorStop(0, '#051f33');
      skullGrad.addColorStop(0.35, '#090821');
      skullGrad.addColorStop(0.85, '#1e062b');
      skullGrad.addColorStop(1, '#3b0738');

      ctx.beginPath();
      ctx.moveTo(-92, 6);
      ctx.lineTo(-90, 68);
      ctx.bezierCurveTo(-88, 118, 92, 122, 108, 62);
      ctx.lineTo(106, -4);
      ctx.closePath();
      ctx.fillStyle = skullGrad;
      ctx.fill();

      // Left cyan neon edge on skullcap
      ctx.save();
      ctx.shadowColor = pal.cyan;
      ctx.shadowBlur = 22 * glowIntensity;
      ctx.strokeStyle = pal.cyan;
      ctx.lineWidth = 3.2;
      ctx.beginPath();
      ctx.moveTo(-92, 6);
      ctx.lineTo(-90, 68);
      ctx.bezierCurveTo(-88, 102, -15, 112, 35, 104);
      ctx.stroke();

      // Inner cyan reflection highlight on skullcap left side
      ctx.strokeStyle = `rgba(${pal.cyanRgb}, 0.55)`;
      ctx.lineWidth = 1.4;
      ctx.beginPath();
      ctx.moveTo(-80, 14);
      ctx.lineTo(-78, 64);
      ctx.bezierCurveTo(-76, 90, -25, 100, 15, 96);
      ctx.stroke();
      ctx.restore();

      // Right magenta neon edge on skullcap
      ctx.save();
      ctx.shadowColor = pal.magenta;
      ctx.shadowBlur = 18 * glowIntensity;
      ctx.strokeStyle = pal.magenta;
      ctx.lineWidth = 2.5;
      ctx.beginPath();
      ctx.moveTo(106, -4);
      ctx.lineTo(108, 62);
      ctx.bezierCurveTo(102, 96, 52, 108, 10, 105);
      ctx.stroke();

      // Internal sketchy contour lines on skullcap right side (like the illustration)
      ctx.strokeStyle = `rgba(${pal.magentaRgb}, 0.42)`;
      ctx.lineWidth = 1.1;
      ctx.beginPath();
      ctx.moveTo(94, 4);
      ctx.lineTo(95, 58);
      ctx.moveTo(84, 15);
      ctx.lineTo(85, 65);
      ctx.stroke();
      ctx.restore();
      ctx.restore();

      // 3. Mortarboard Top Diamond Platform (3D perspective)
      const pTop = { x: 22, y: -112 };
      const pRight = { x: 205, y: -18 };
      const pBottom = { x: -42, y: 42 };
      const pLeft = { x: -205, y: -22 };
      const rimThickness = 7;

      // Bottom lip of the mortarboard for 3D thickness
      ctx.save();
      ctx.beginPath();
      ctx.moveTo(pLeft.x, pLeft.y);
      ctx.lineTo(pBottom.x, pBottom.y);
      ctx.lineTo(pRight.x, pRight.y);
      ctx.lineTo(pRight.x, pRight.y + rimThickness);
      ctx.lineTo(pBottom.x, pBottom.y + rimThickness);
      ctx.lineTo(pLeft.x, pLeft.y + rimThickness);
      ctx.closePath();
      const rimGrad = ctx.createLinearGradient(pLeft.x, 0, pRight.x, 0);
      rimGrad.addColorStop(0, pal.cyan);
      rimGrad.addColorStop(0.55, '#19143b');
      rimGrad.addColorStop(1, pal.magenta);
      ctx.fillStyle = rimGrad;
      ctx.fill();
      ctx.restore();

      // Main diamond fill
      ctx.save();
      ctx.beginPath();
      ctx.moveTo(pTop.x, pTop.y);
      ctx.lineTo(pRight.x, pRight.y);
      ctx.lineTo(pBottom.x, pBottom.y);
      ctx.lineTo(pLeft.x, pLeft.y);
      ctx.closePath();

      const boardGrad = ctx.createLinearGradient(pLeft.x, pBottom.y, pTop.x + 60, pTop.y);
      boardGrad.addColorStop(0, '#071b34');
      boardGrad.addColorStop(0.45, '#13092c');
      boardGrad.addColorStop(0.82, '#5c064c');
      boardGrad.addColorStop(1, pal.magenta);
      ctx.fillStyle = boardGrad;
      ctx.fill();

      // Clip inside the diamond to render starry speckles & internal wireframe lines
      ctx.clip();

      // Starry luminescent speckles across the mortarboard surface
      for (let i = 0; i < dustSpeckles.length; i++) {
        const sp = dustSpeckles[i];
        const twinkle = 0.35 + 0.65 * Math.sin(t * 2.4 + sp.phase);
        const isPink = sp.offsetX > -10;
        ctx.fillStyle = isPink
          ? `rgba(255, 165, 225, ${sp.alpha * twinkle})`
          : `rgba(130, 245, 255, ${sp.alpha * twinkle})`;
        ctx.beginPath();
        ctx.arc(sp.offsetX * 0.65, sp.offsetY * 0.45 - 30, sp.size, 0, Math.PI * 2);
        ctx.fill();
      }

      // Subtle geometric tech grid lines inside the mortarboard
      ctx.strokeStyle = `rgba(${pal.magentaRgb}, 0.22)`;
      ctx.lineWidth = 0.9;
      ctx.beginPath();
      ctx.moveTo(pTop.x - 30, pTop.y + 15);
      ctx.lineTo(pRight.x - 25, pRight.y - 5);
      ctx.moveTo(pTop.x, pTop.y);
      ctx.lineTo(pBottom.x, pBottom.y);
      ctx.moveTo(pLeft.x + 30, pLeft.y);
      ctx.lineTo(pRight.x - 30, pRight.y);
      ctx.stroke();
      ctx.restore();

      // Neon glowing border along the Top-Right & Bottom-Right of the diamond (Magenta/Pink)
      ctx.save();
      ctx.shadowColor = pal.magenta;
      ctx.shadowBlur = 25 * glowIntensity;
      ctx.strokeStyle = '#ff55a3';
      ctx.lineWidth = 3.2;
      ctx.beginPath();
      ctx.moveTo(pLeft.x * 0.35 + pTop.x * 0.65, pLeft.y * 0.35 + pTop.y * 0.65);
      ctx.lineTo(pTop.x, pTop.y);
      ctx.lineTo(pRight.x, pRight.y);
      ctx.lineTo(pBottom.x * 0.3 + pRight.x * 0.7, pBottom.y * 0.3 + pRight.y * 0.7);
      ctx.stroke();

      // Inner double-line rim on top-right (matching reference image detail)
      ctx.strokeStyle = 'rgba(255, 210, 235, 0.8)';
      ctx.lineWidth = 1.4;
      ctx.beginPath();
      ctx.moveTo(pTop.x - 4, pTop.y + 8);
      ctx.lineTo(pRight.x - 12, pRight.y + 2);
      ctx.stroke();
      ctx.restore();

      // Neon glowing border along the Bottom-Left & Top-Left of the diamond (Cyan)
      ctx.save();
      ctx.shadowColor = pal.cyan;
      ctx.shadowBlur = 24 * glowIntensity;
      ctx.strokeStyle = pal.cyan;
      ctx.lineWidth = 3.2;
      ctx.beginPath();
      ctx.moveTo(pLeft.x * 0.35 + pTop.x * 0.65, pLeft.y * 0.35 + pTop.y * 0.65);
      ctx.lineTo(pLeft.x, pLeft.y);
      ctx.lineTo(pBottom.x, pBottom.y);
      ctx.lineTo(pBottom.x * 0.3 + pRight.x * 0.7, pBottom.y * 0.3 + pRight.y * 0.7);
      ctx.stroke();
      ctx.restore();

      // 4. Center Button & Animated Swaying Tassel
      const btnX = 6;
      const btnY = -36;

      // Glowing accent line from center button toward upper-right edge
      ctx.save();
      ctx.shadowColor = pal.magenta;
      ctx.shadowBlur = 14 * glowIntensity;
      ctx.strokeStyle = '#ff7ec4';
      ctx.lineWidth = 2.4;
      ctx.beginPath();
      ctx.moveTo(btnX, btnY);
      ctx.lineTo(126, -54);
      ctx.stroke();
      ctx.restore();

      // Center cord button
      ctx.save();
      ctx.shadowColor = pal.magenta;
      ctx.shadowBlur = 16 * glowIntensity;
      ctx.fillStyle = '#ff2a8d';
      ctx.strokeStyle = '#ff9ed2';
      ctx.lineWidth = 1.8;
      ctx.beginPath();
      ctx.ellipse(btnX, btnY, 13, 8.5, -0.15, 0, Math.PI * 2);
      ctx.fill();
      ctx.stroke();
      ctx.restore();

      // Animated Tassel physics (pendulum sway in the breeze)
      const sway = Math.sin(t * 2.1) * 9 + Math.cos(t * 1.3) * 4;
      const drapeX = 62;
      const drapeY = -2;
      const knotX = 56 + sway * 0.45;
      const knotY = 92;
      const tasselBottomY = 186;

      // Tassel cord draping across the board and hanging down
      ctx.save();
      ctx.shadowColor = pal.magenta;
      ctx.shadowBlur = 16 * glowIntensity;
      ctx.strokeStyle = '#ff2e93';
      ctx.lineWidth = 3.2;
      ctx.lineCap = 'round';
      ctx.beginPath();
      ctx.moveTo(btnX + 8, btnY + 3);
      ctx.quadraticCurveTo(42, -24, drapeX, drapeY);
      ctx.quadraticCurveTo(64 + sway * 0.2, 45, knotX, knotY);
      ctx.stroke();

      // Tassel band/ring node
      ctx.fillStyle = '#ff1f8a';
      ctx.strokeStyle = '#ffa6d8';
      ctx.lineWidth = 1.6;
      ctx.beginPath();
      ctx.arc(knotX, knotY, 8.5, 0, Math.PI * 2);
      ctx.fill();
      ctx.stroke();

      // Tassel brush fringes (individual glowing strands)
      const strandCount = 15;
      for (let s = 0; s < strandCount; s++) {
        const ratio = s / (strandCount - 1) - 0.5; // -0.5 to 0.5
        const spreadBottom = ratio * 30 + sway * 1.15;
        const strandWave = Math.sin(t * 3 + s * 0.5) * 1.8;

        ctx.strokeStyle = s % 3 === 0 ? '#ff85c8' : pal.magenta;
        ctx.lineWidth = s % 4 === 0 ? 1.8 : 1.2;
        ctx.beginPath();
        ctx.moveTo(knotX + ratio * 8, knotY + 6);
        ctx.quadraticCurveTo(
          knotX + spreadBottom * 0.5 + strandWave,
          (knotY + tasselBottomY) * 0.5,
          knotX + spreadBottom,
          tasselBottomY + Math.abs(ratio) * 6
        );
        ctx.stroke();
      }
      ctx.restore();

      ctx.restore();
    };

    const render = () => {
      if (!isPaused) {
        time += 0.016 * animationSpeed;
      }

      // Smooth mouse parallax interpolation
      parallaxX += (mouseRef.current.targetX - parallaxX) * 0.06;
      parallaxY += (mouseRef.current.targetY - parallaxY) * 0.06;

      // 1. Deep Atmospheric Gradient Background (Teal Top-Left -> Midnight Navy -> Magenta Bottom-Right)
      const bgGrad = ctx.createLinearGradient(0, 0, width, height);
      bgGrad.addColorStop(0, pal.bgLeft);
      bgGrad.addColorStop(0.46, pal.bgMid);
      bgGrad.addColorStop(1, pal.bgRight);
      ctx.fillStyle = bgGrad;
      ctx.fillRect(0, 0, width, height);

      // Ambient top-left teal glow
      const topLeftGlow = ctx.createRadialGradient(
        width * 0.08,
        height * 0.12,
        10,
        width * 0.12,
        height * 0.18,
        width * 0.55
      );
      topLeftGlow.addColorStop(0, `rgba(${pal.cyanRgb}, ${0.22 * glowIntensity})`);
      topLeftGlow.addColorStop(0.5, `rgba(${pal.cyanRgb}, 0.06)`);
      topLeftGlow.addColorStop(1, 'rgba(0, 0, 0, 0)');
      ctx.fillStyle = topLeftGlow;
      ctx.fillRect(0, 0, width, height);

      // Ambient bottom-right magenta glow
      const bottomRightGlow = ctx.createRadialGradient(
        width * 0.88,
        height * 0.78,
        20,
        width * 0.82,
        height * 0.7,
        width * 0.55
      );
      bottomRightGlow.addColorStop(0, `rgba(${pal.magentaRgb}, ${0.28 * glowIntensity})`);
      bottomRightGlow.addColorStop(0.5, `rgba(${pal.purpleRgb}, 0.1)`);
      bottomRightGlow.addColorStop(1, 'rgba(0, 0, 0, 0)');
      ctx.fillStyle = bottomRightGlow;
      ctx.fillRect(0, 0, width, height);

      // 2. Update & Draw Plexus Network (Lines & Glowing Nodes)
      const maxDist = connectionDistance;
      const maxDistSq = maxDist * maxDist;

      for (let i = 0; i < particles.length; i++) {
        const p = particles[i];
        if (!isPaused) {
          p.x += p.vx * animationSpeed;
          p.y += p.vy * animationSpeed;
          p.pulsePhase += p.pulseSpeed * animationSpeed;

          // Subtle attraction/repulsion when cursor is nearby
          if (interactiveMouse && mouseRef.current.active) {
            const dx = mouseRef.current.x - p.x;
            const dy = mouseRef.current.y - p.y;
            const distSq = dx * dx + dy * dy;
            if (distSq < 28000 && distSq > 10) {
              const force = (1 - distSq / 28000) * 0.035;
              p.x += dx * force;
              p.y += dy * force;
            }
          }

          // Wrap around screen edges smoothly
          if (p.x < -30) p.x = width + 30;
          if (p.x > width + 30) p.x = -30;
          if (p.y < -30) p.y = height + 30;
          if (p.y > height + 30) p.y = -30;
        }
      }

      // Draw constellation connections
      ctx.lineWidth = 1.05;
      for (let i = 0; i < particles.length; i++) {
        const p1 = particles[i];
        for (let j = i + 1; j < particles.length; j++) {
          const p2 = particles[j];
          const dx = p1.x - p2.x;
          const dy = p1.y - p2.y;
          const dSq = dx * dx + dy * dy;

          if (dSq < maxDistSq) {
            const alpha = (1 - Math.sqrt(dSq) / maxDist) * 0.52 * glowIntensity;
            const rgb =
              p1.colorType === 'cyan' && p2.colorType === 'cyan'
                ? pal.cyanRgb
                : p1.colorType === 'magenta' || p2.colorType === 'magenta'
                ? pal.magentaRgb
                : pal.purpleRgb;

            ctx.strokeStyle = `rgba(${rgb}, ${Math.min(alpha, 0.85)})`;
            ctx.beginPath();
            ctx.moveTo(p1.x + parallaxX * 8, p1.y + parallaxY * 8);
            ctx.lineTo(p2.x + parallaxX * 8, p2.y + parallaxY * 8);
            ctx.stroke();
          }
        }
      }

      // Draw glowing constellation vertices (nodes)
      for (let i = 0; i < particles.length; i++) {
        const p = particles[i];
        const pulse = 0.65 + 0.35 * Math.sin(p.pulsePhase);
        const rgb =
          p.colorType === 'cyan'
            ? pal.cyanRgb
            : p.colorType === 'magenta'
            ? pal.magentaRgb
            : pal.purpleRgb;

        const px = p.x + parallaxX * 8;
        const py = p.y + parallaxY * 8;

        // Outer halo
        ctx.fillStyle = `rgba(${rgb}, ${0.28 * pulse * glowIntensity})`;
        ctx.beginPath();
        ctx.arc(px, py, p.radius * 2.9, 0, Math.PI * 2);
        ctx.fill();

        // Bright core
        ctx.fillStyle = `rgba(255, 255, 255, ${0.75 * pulse})`;
        ctx.beginPath();
        ctx.arc(px, py, p.radius * 0.9, 0, Math.PI * 2);
        ctx.fill();
      }

      // 3. Draw the 3D Neon Graduation Cap on the right-center (responsive scaling)
      if (showCap) {
        const capX = width > 768 ? width * 0.69 : width * 0.55;
        const capY = height > 600 ? height * 0.39 : height * 0.36;
        const baseScale = Math.min(width / 1150, height / 680);
        const responsiveScale = Math.max(0.58, Math.min(baseScale * 1.15, 1.45));

        drawGraduationCap(ctx, capX, capY, responsiveScale, time);
      }

      animationFrameId = requestAnimationFrame(render);
    };

    render();

    return () => {
      cancelAnimationFrame(animationFrameId);
      window.removeEventListener('resize', handleResize);
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('mouseleave', handleMouseLeave);
    };
  }, [
    particleCount,
    connectionDistance,
    animationSpeed,
    capFloatRange,
    glowIntensity,
    interactiveMouse,
    isPaused,
    colorPreset,
    showCap
  ]);

  return (
    <div className={`neon-graduation-background ${className}`} aria-hidden="true">
      <canvas ref={canvasRef} />
    </div>
  );
}

export default NeonGraduationBackground;
