/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.psygrid.collection.entry.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * See http://www.curious-creature.org/2005/02/15/wait-with-style-in-swing/ for
 * more information and original source by Romain Guy.
 * 
 * Some very minor cleanups (mostly to remove warnings) were done to this
 * version.
 * 
 * @author Romain Guy
 */
public class InfiniteProgressPanel extends JComponent implements MouseListener {
    private static final long serialVersionUID = 1L;
    
    public static final int DEFAULT_RAMP_DELAY = 300;
    public static final int DEFAULT_BARS_COUNT = 14;
    public static final float DEFAULT_FPS = 15.0f;
    public static final float DEFAULT_SHIELD = 0.70f;

    protected Area[] ticker = null;

    protected Thread animation = null;

    protected boolean started = false;
    
    protected int alphaLevel = 0;

    protected int rampDelay = DEFAULT_RAMP_DELAY;

    protected float shield = DEFAULT_SHIELD;

    protected String text = ""; //$NON-NLS-1$

    protected int barsCount = DEFAULT_BARS_COUNT;

    protected float fps = DEFAULT_FPS;

    protected RenderingHints hints = null;
    
    private volatile boolean waitingToStart = false;
    
    public InfiniteProgressPanel() {
        this(""); //$NON-NLS-1$
    }

    public InfiniteProgressPanel(String text) {
        this(text, 14);
    }

    public InfiniteProgressPanel(String text, int barsCount) {
        this(text, barsCount, 0.70f);
    }

    public InfiniteProgressPanel(String text, int barsCount, float shield) {
        this(text, barsCount, shield, 15.0f);
    }

    public InfiniteProgressPanel(String text, int barsCount, float shield,
            float fps) {
        this(text, barsCount, shield, fps, 300);
    }

    public InfiniteProgressPanel(String text, int barsCount, float shield,
            float fps, int rampDelay) {
        this.text = text;
        this.rampDelay = rampDelay >= 0 ? rampDelay : 0;
        this.shield = shield >= 0.0f ? shield : 0.0f;
        this.fps = fps > 0.0f ? fps : 15.0f;
        this.barsCount = barsCount > 0 ? barsCount : 14;

        this.hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        this.hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    public void setText(String text) {
        repaint();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void start() {
        addMouseListener(this);
        setVisible(true);
        Timer timer = new Timer(2000, new ActionListener()  {
            public void actionPerformed(ActionEvent e) {
                if (waitingToStart) {
                    ticker = buildTicker();
                    animation = new Thread(new Animator(true));
                    animation.start();
                }
            }
        });
        timer.setRepeats(false);
        waitingToStart = true;
        timer.start();
    }

    public void stop() {
        if (waitingToStart) {
            waitingToStart = false;
            removeMouseListener(this);
            setVisible(false);
        }
        if (animation != null) {
            animation.interrupt();
            animation = null;
            animation = new Thread(new Animator(false));
            animation.start();
        }
    }

    public void interrupt() {
        if (animation != null) {
            animation.interrupt();
            animation = null;

            removeMouseListener(this);
            setVisible(false);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (started) {
            int width = getWidth();
            int height = getHeight();

            double maxY = 0.0;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(hints);

            g2.setColor(new Color(255, 255, 255, (int) (alphaLevel * shield)));
            g2.fillRect(0, 0, width, height);

            for (int i = 0; i < ticker.length; i++) {
                int channel = 224 - 128 / (i + 1);
                g2.setColor(new Color(channel, channel, channel, alphaLevel));
                g2.fill(ticker[i]);

                Rectangle2D bounds = ticker[i].getBounds2D();
                if (bounds.getMaxY() > maxY)
                    maxY = bounds.getMaxY();
            }

            if (text != null && text.length() > 0) {
                FontRenderContext context = g2.getFontRenderContext();
                TextLayout layout = new TextLayout(text, getFont(), context);
                Rectangle2D bounds = layout.getBounds();
                g2.setColor(getForeground());
                layout.draw(g2, (float) (width - bounds.getWidth()) / 2,
                        (float) (maxY + layout.getLeading() + 2 * layout
                                .getAscent()));
            }
        }
    }

    private Area[] buildTicker() {
        Area[] localTicker = new Area[barsCount];
        Point2D.Double center = new Point2D.Double((double) getWidth() / 2,
                (double) getHeight() / 2);
        double fixedAngle = 2.0 * Math.PI / barsCount;

        for (double i = 0.0; i < barsCount; i++) {
            Area primitive = buildPrimitive();

            AffineTransform toCenter = AffineTransform.getTranslateInstance(
                    center.getX(), center.getY());
            AffineTransform toBorder = AffineTransform.getTranslateInstance(
                    45.0, -6.0);
            AffineTransform toCircle = AffineTransform.getRotateInstance(-i
                    * fixedAngle, center.getX(), center.getY());

            AffineTransform toWheel = new AffineTransform();
            toWheel.concatenate(toCenter);
            toWheel.concatenate(toBorder);

            primitive.transform(toWheel);
            primitive.transform(toCircle);

            localTicker[(int) i] = primitive;
        }

        return localTicker;
    }

    private Area buildPrimitive() {
        Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
        Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
        Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

        Area tick = new Area(body);
        tick.add(new Area(head));
        tick.add(new Area(tail));

        return tick;
    }

    protected class Animator implements Runnable {
        private boolean rampUp = true;

        protected Animator(boolean rampUp) {
            this.rampUp = rampUp;
        }

        public void run() {
            Point2D.Double center = new Point2D.Double((double) getWidth() / 2,
                    (double) getHeight() / 2);
            double fixedIncrement = 2.0 * Math.PI / barsCount;
            AffineTransform toCircle = AffineTransform.getRotateInstance(
                    fixedIncrement, center.getX(), center.getY());

            long start = System.currentTimeMillis();
            if (rampDelay == 0)
                alphaLevel = rampUp ? 255 : 0;

            started = true;
            boolean inRamp = rampUp;

            while (!Thread.interrupted()) {
                if (!inRamp) {
                    for (int i = 0; i < ticker.length; i++)
                        ticker[i].transform(toCircle);
                }

                repaint();

                if (rampUp) {
                    if (alphaLevel < 255) {
                        alphaLevel = (int) (255 * (System.currentTimeMillis() - start) / rampDelay);
                        if (alphaLevel >= 255) {
                            alphaLevel = 255;
                            inRamp = false;
                        }
                    }
                } else if (alphaLevel > 0) {
                    alphaLevel = (int) (255 - (255 * (System
                            .currentTimeMillis() - start) / rampDelay));
                    if (alphaLevel <= 0) {
                        alphaLevel = 0;
                        break;
                    }
                } else  {
                    break;
                }

                try {
                    Thread.sleep(inRamp ? 10 : (int) (1000 / fps));
                } catch (InterruptedException ie) {
                    break;
                }
                Thread.yield();
            }

            if (!rampUp) {
                started = false;
                repaint();

                setVisible(false);
                removeMouseListener(InfiniteProgressPanel.this);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }

    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }

    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
}