package com.nfcpay.view.dialogs;

import com.nfcpay.model.Card;
import com.nfcpay.model.Merchant;
import com.nfcpay.util.UIUtils;
import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Professional Payment Confirmation Dialog
 */
public class PaymentConfirmDialog extends JDialog {
    private boolean confirmed = false;
    
    public PaymentConfirmDialog(JFrame parent, Card card, Merchant merchant, 
                               BigDecimal amount, String description) {
        super(parent, "Confirm Payment", true);
        setupDialog(card, merchant, amount, description);
    }
    
    private void setupDialog(Card card, Merchant merchant, BigDecimal amount, String description) {
        setLayout(new BorderLayout());
        setBackground(UIUtils.getBackgroundColor());
        
        // Professional Header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, UIUtils.PRIMARY, getWidth(), 0, UIUtils.PRIMARY.brighter());
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        
        JLabel titleLabel = new JLabel("💳 Confirm Payment");
        titleLabel.setFont(UIUtils.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Professional Content Card
        JPanel contentCard = UIUtils.createCard();
        contentCard.setLayout(new GridBagLayout());
        contentCard.setBackground(UIUtils.getSurfaceColor());
        contentCard.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Payment Details with Icons
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel cardLabel = new JLabel("💳 Card:");
        cardLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cardLabel.setForeground(UIUtils.getTextColor());
        contentCard.add(cardLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JLabel cardValue = new JLabel(card.getCardName() + " (" + card.getCardType() + ")");
        cardValue.setFont(UIUtils.FONT_BODY);
        cardValue.setForeground(UIUtils.getTextColor());
        contentCard.add(cardValue, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel merchantLabel = new JLabel("🏦 Merchant:");
        merchantLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        merchantLabel.setForeground(UIUtils.getTextColor());
        contentCard.add(merchantLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JLabel merchantValue = new JLabel(merchant.getMerchantName());
        merchantValue.setFont(UIUtils.FONT_BODY);
        merchantValue.setForeground(UIUtils.getTextColor());
        contentCard.add(merchantValue, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel amountLabel = new JLabel("💰 Amount:");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountLabel.setForeground(UIUtils.getTextColor());
        contentCard.add(amountLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JLabel amountValue = new JLabel(String.format("$%.2f", amount));
        amountValue.setFont(new Font("SansSerif", Font.BOLD, 18));
        amountValue.setForeground(UIUtils.DANGER);
        contentCard.add(amountValue, gbc);
        
        if (description != null && !description.isEmpty()) {
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel descLabel = new JLabel("📝 Description:");
            descLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            descLabel.setForeground(UIUtils.getTextColor());
            contentCard.add(descLabel, gbc);
            
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JLabel descValue = new JLabel(description);
            descValue.setFont(UIUtils.FONT_BODY);
            descValue.setForeground(UIUtils.getTextColor());
            contentCard.add(descValue, gbc);
        }
        
        // Professional Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIUtils.SPACING_MD, UIUtils.SPACING_SM));
        buttonPanel.setBackground(UIUtils.getBackgroundColor());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, 0, UIUtils.SPACING_SM, 0));
        
        CustomButton confirmButton = CustomButton.createSuccessButton("✅ Confirm Payment");
        confirmButton.setPreferredSize(new Dimension(160, 45));
        CustomButton cancelButton = CustomButton.createSecondaryButton("❌ Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 45));
        
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UIUtils.getBackgroundColor());
        mainContainer.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        mainContainer.add(contentCard, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(500, 350);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(confirmButton);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}