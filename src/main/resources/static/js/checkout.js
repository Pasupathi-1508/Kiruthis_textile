(function () {
    'use strict';

    let cart = JSON.parse(localStorage.getItem('kiruthis_cart') || '[]');
    let freeGiftChoice = localStorage.getItem('kiruthis_free_gift') || 'FREE_ROUND_NECK_TSHIRT';
    let freeGiftSize = localStorage.getItem('kiruthis_free_gift_size') || 'L';
    let selectedUpiApp = 'GPay_UPI';

    const FREE_GIFT_THRESHOLD = 2000;
    const COD_FEE = 40;

    function getSubtotal() {
        return cart.reduce((sum, item) => sum + (Number(item.sellingPrice) * Number(item.quantity)), 0);
    }

    function isTamilNaduSelected() {
        const stateVal = document.getElementById('chkState').value;
        const pinVal = (document.getElementById('chkPincode').value || '').trim();
        if (pinVal.length === 6 && /^6[0-4]/.test(pinVal)) {
            return true;
        }
        return stateVal === 'Tamil Nadu';
    }

    function getSelectedPaymentMode() {
        const checked = document.querySelector('input[name="chkPaymentMode"]:checked');
        return checked ? checked.value : 'ONLINE_PREPAID';
    }

    // =========================================================================
    // 1. RENDER ABOVE-₹2,000 FREE T-SHIRT / SHORTS SELECTOR
    // =========================================================================
    function renderFreeGiftSelector() {
        const subtotal = getSubtotal();
        const container = document.getElementById('freeGiftDynamicBody');

        if (subtotal >= FREE_GIFT_THRESHOLD) {
            container.innerHTML = `
                <div class="gift-selector-card">
                    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px;">
                        <div>
                            <span style="background: #059669; color: #fff; font-size: 11px; font-weight: 800; padding: 3px 8px; border-radius: 6px;">🎉 UNLOCKED! ORDER ≥ ₹2,000</span>
                            <h4 style="font-size: 15px; font-weight: 800; color: #0f172a; margin-top: 6px;">
                                Choose Your FREE Branded Garment + Assured Showroom Gift:
                            </h4>
                        </div>
                        <div>
                            <label style="font-size: 11.5px; font-weight: 800; color: #78350f;">Select Freebie Size:</label>
                            <select id="chkGiftSizeSelect" style="padding: 6px 10px; border-radius: 8px; border: 2px solid #0f172a; font-weight: 800;">
                                <option value="M" ${freeGiftSize === 'M' ? 'selected' : ''}>Size M (40")</option>
                                <option value="L" ${freeGiftSize === 'L' ? 'selected' : ''}>Size L (42")</option>
                                <option value="XL" ${freeGiftSize === 'XL' ? 'selected' : ''}>Size XL (44")</option>
                                <option value="XXL" ${freeGiftSize === 'XXL' ? 'selected' : ''}>Size XXL (46")</option>
                            </select>
                        </div>
                    </div>

                    <div class="gift-radio-grid">
                        <label class="gift-option-box ${freeGiftChoice === 'FREE_ROUND_NECK_TSHIRT' ? 'selected' : ''}" data-gift-box="FREE_ROUND_NECK_TSHIRT">
                            <input type="radio" name="giftChoiceRadio" value="FREE_ROUND_NECK_TSHIRT" ${freeGiftChoice === 'FREE_ROUND_NECK_TSHIRT' ? 'checked' : ''} />
                            <div>
                                <strong style="font-size: 13px; display: block;">👕 Free Branded Round Neck T-Shirt</strong>
                                <span style="font-size: 11.5px; color: #475569;">100% Combed Tiruppur Cotton T-Shirt + Assured Gift (₹0)</span>
                            </div>
                        </label>

                        <label class="gift-option-box ${freeGiftChoice === 'FREE_BRANDED_SHORTS' ? 'selected' : ''}" data-gift-box="FREE_BRANDED_SHORTS">
                            <input type="radio" name="giftChoiceRadio" value="FREE_BRANDED_SHORTS" ${freeGiftChoice === 'FREE_BRANDED_SHORTS' ? 'checked' : ''} />
                            <div>
                                <strong style="font-size: 13px; display: block;">🩳 Free Branded Casual Shorts</strong>
                                <span style="font-size: 11.5px; color: #475569;">Branded Cotton Lounge/Casual Shorts + Assured Gift (₹0)</span>
                            </div>
                        </label>
                    </div>
                </div>
            `;

            container.querySelectorAll('input[name="giftChoiceRadio"]').forEach(r => {
                r.addEventListener('change', (e) => {
                    freeGiftChoice = e.target.value;
                    localStorage.setItem('kiruthis_free_gift', freeGiftChoice);
                    renderFreeGiftSelector();
                    renderOrderSummary();
                });
            });

            document.getElementById('chkGiftSizeSelect').addEventListener('change', (e) => {
                freeGiftSize = e.target.value;
                localStorage.setItem('kiruthis_free_gift_size', freeGiftSize);
                renderOrderSummary();
            });
        } else {
            const diff = FREE_GIFT_THRESHOLD - subtotal;
            container.innerHTML = `
                <div style="background: #fffbeb; border: 1px dashed #f59e0b; padding: 14px; border-radius: 12px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
                    <div style="font-size: 13px; color: #78350f;">
                        🎁 You are only <strong>₹${diff.toLocaleString('en-IN')}</strong> away from unlocking a <strong>FREE Branded Round Neck T-Shirt OR Shorts + Assured Gift!</strong>
                    </div>
                    <a href="/" class="btn-gold" style="text-decoration: none;">+ Add ₹${diff.toLocaleString('en-IN')} More</a>
                </div>
            `;
        }
    }

    // =========================================================================
    // 2. RENDER BAG ITEMS & LIVE GST TAX BREAKDOWN (CGST+SGST vs IGST)
    // =========================================================================
    function renderOrderSummary() {
        const listEl = document.getElementById('checkoutBagItemsList');
        const taxBox = document.getElementById('checkoutTaxBreakdownBox');
        const submitBtn = document.getElementById('confirmPlaceOrderBtn');

        if (cart.length === 0) {
            listEl.innerHTML = `
                <div style="padding: 28px; text-align: center; color: #64748b;">
                    <p style="font-weight: 800; font-size: 14px;">Your Shopping Bag is Empty</p>
                    <a href="/" class="btn-gold" style="display: inline-block; margin-top: 10px; text-decoration: none;">Browse Showroom Catalogue</a>
                </div>
            `;
            taxBox.innerHTML = '';
            submitBtn.disabled = true;
            return;
        }

        submitBtn.disabled = false;

        listEl.innerHTML = cart.map((item, idx) => `
            <div style="display: flex; justify-content: space-between; align-items: center; gap: 10px; padding: 10px; border-radius: 10px; background: #f8fafc; border: 1px solid #e2e8f0;">
                <div style="flex: 1;">
                    <strong style="font-size: 12.5px; display: block; color: #0f172a;">${item.brandName} — ${item.title}</strong>
                    <span style="font-size: 11px; color: #475569;">Color: ${item.colorName} • Size: ${item.sizeLabel} • SKU: ${item.skuCode}</span>
                    <span style="display: block; font-size: 12px; font-weight: 800; color: #1e3a8a; margin-top: 2px;">
                        ₹${Number(item.sellingPrice).toLocaleString('en-IN')} × ${item.quantity} = ₹${(item.sellingPrice * item.quantity).toLocaleString('en-IN')}
                        <small style="color: #059669;">(${item.gstPercentage || 5}% GST)</small>
                    </span>
                </div>
                <div class="qty-controls">
                    <button type="button" class="qty-btn" data-chk-dec="${idx}">-</button>
                    <span style="font-size: 12px; font-weight: 800;">${item.quantity}</span>
                    <button type="button" class="qty-btn" data-chk-inc="${idx}">+</button>
                </div>
            </div>
        `).join('');

        listEl.querySelectorAll('[data-chk-dec]').forEach(btn => {
            btn.addEventListener('click', () => {
                const idx = Number(btn.getAttribute('data-chk-dec'));
                if (cart[idx].quantity > 1) cart[idx].quantity -= 1;
                else cart.splice(idx, 1);
                localStorage.setItem('kiruthis_cart', JSON.stringify(cart));
                renderFreeGiftSelector();
                renderOrderSummary();
            });
        });

        listEl.querySelectorAll('[data-chk-inc]').forEach(btn => {
            btn.addEventListener('click', () => {
                const idx = Number(btn.getAttribute('data-chk-inc'));
                cart[idx].quantity += 1;
                localStorage.setItem('kiruthis_cart', JSON.stringify(cart));
                renderFreeGiftSelector();
                renderOrderSummary();
            });
        });

        // Calculate exact GST split
        let totalTaxable = 0;
        let totalTax = 0;
        cart.forEach(item => {
            const lineTotal = Number(item.sellingPrice) * Number(item.quantity);
            const gstPct = Number(item.gstPercentage || 5);
            const taxable = lineTotal / (1 + (gstPct / 100));
            totalTaxable += taxable;
            totalTax += (lineTotal - taxable);
        });

        const subtotal = getSubtotal();
        const intraTN = isTamilNaduSelected();
        const payMode = getSelectedPaymentMode();
        const codFee = payMode === 'COD' ? COD_FEE : 0;
        const grandTotal = subtotal + codFee;

        const halfTax = (totalTax / 2).toFixed(2);
        const fullTax = totalTax.toFixed(2);

        taxBox.innerHTML = `
            <div class="summary-line">
                <span>Taxable Garment Value</span>
                <strong>₹${totalTaxable.toFixed(2)}</strong>
            </div>
            ${intraTN ? `
            <div class="summary-line" style="color: #334155; font-size: 12px;">
                <span>CGST (Tamil Nadu State Code 33)</span>
                <span>₹${halfTax}</span>
            </div>
            <div class="summary-line" style="color: #334155; font-size: 12px;">
                <span>SGST (Tamil Nadu State Code 33)</span>
                <span>₹${halfTax}</span>
            </div>` : `
            <div class="summary-line" style="color: #334155; font-size: 12px;">
                <span>IGST (Inter-State Supply)</span>
                <span>₹${fullTax}</span>
            </div>`}
            <div class="summary-line">
                <span>Items Total (Incl. GST)</span>
                <strong>₹${subtotal.toLocaleString('en-IN')}</strong>
            </div>
            <div class="summary-line">
                <span>All-India Insured Courier</span>
                <strong style="color: #059669;">FREE</strong>
            </div>
            <div class="summary-line">
                <span>COD Handling Fee</span>
                <strong>${codFee > 0 ? '₹' + codFee : '₹0 (Prepaid Benefit)'}</strong>
            </div>
            ${subtotal >= FREE_GIFT_THRESHOLD ? `
            <div class="summary-line" style="color: #059669; font-weight: 800; background: #ecfdf5; padding: 6px 8px; border-radius: 6px;">
                <span>🎁 ${freeGiftChoice === 'FREE_BRANDED_SHORTS' ? 'Free Branded Shorts' : 'Free Round Neck T-Shirt'} (Size ${freeGiftSize}) + Assured Gift</span>
                <span>FREE (₹0)</span>
            </div>` : ''}
            <div class="summary-line summary-total">
                <span>Grand Total Payable</span>
                <span>₹${grandTotal.toLocaleString('en-IN')}</span>
            </div>
        `;
    }

    // =========================================================================
    // 3. SUBMIT ORDER TO SPRING BOOT + MYSQL (/api/v1/orders/place)
    // =========================================================================
    async function submitOrderToBackend() {
        const errBox = document.getElementById('checkoutErrorBox');
        errBox.classList.add('hidden');

        const payMode = getSelectedPaymentMode();
        const subtotal = getSubtotal();

        const payload = {
            customerName: document.getElementById('chkName').value.trim(),
            customerPhone: document.getElementById('chkPhone').value.trim(),
            customerEmail: document.getElementById('chkEmail').value.trim(),
            shippingAddressLine1: document.getElementById('chkAddress1').value.trim(),
            shippingLandmark: document.getElementById('chkLandmark').value.trim(),
            shippingPincode: document.getElementById('chkPincode').value.trim(),
            shippingCity: document.getElementById('chkCity').value.trim(),
            shippingState: document.getElementById('chkState').value,
            paymentMode: payMode,
            codOtp: document.getElementById('codOtpCodeInput').value.trim(),
            gatewayPaymentId: payMode === 'ONLINE_PREPAID' ? (selectedUpiApp + '_' + (Date.now() % 1000000)) : null,
            freeGiftChoice: subtotal >= FREE_GIFT_THRESHOLD ? freeGiftChoice : 'NONE',
            freeGiftSize: subtotal >= FREE_GIFT_THRESHOLD ? freeGiftSize : null,
            items: cart.map(item => ({
                variantId: item.variantId,
                quantity: item.quantity
            }))
        };

        const btn = document.getElementById('confirmPlaceOrderBtn');
        btn.disabled = true;
        btn.textContent = '⏳ Locking Stock & Generating GST Invoice in MySQL...';

        try {
            const response = await fetch('/api/v1/orders/place', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || 'Failed to place order');
            }

            // Clear Shopping Bag after successful MySQL commit
            localStorage.removeItem('kiruthis_cart');
            cart = [];

            renderConfirmedOrderInvoice(data);
        } catch (err) {
            errBox.textContent = '⚠️ ' + err.message;
            errBox.classList.remove('hidden');
            btn.disabled = false;
            btn.textContent = '🔒 Confirm & Place Order in MySQL';
        }
    }

    // =========================================================================
    // 4. RENDER PRINTABLE GST TAX INVOICE & ORDER CONFIRMATION
    // =========================================================================
    function renderConfirmedOrderInvoice(order) {
        document.getElementById('checkoutMainWorkspace').classList.add('hidden');
        const section = document.getElementById('orderSuccessReceiptSection');
        const container = document.getElementById('printableGstInvoiceContainer');

        const isIntraTN = order.shippingStateCode === '33';
        const giftLabel = order.freeGiftChoice === 'FREE_BRANDED_SHORTS'
            ? 'FREE Branded Casual Shorts (Size ' + order.freeGiftSize + ') + Assured Showroom Gift'
            : 'FREE Branded Round Neck T-Shirt (Size ' + order.freeGiftSize + ') + Assured Showroom Gift';

        const rowsHtml = (order.items || []).map((it, idx) => `
            <tr style="border-bottom: 1px solid #cbd5e1;">
                <td style="padding: 8px;">${idx + 1}</td>
                <td style="padding: 8px;">
                    <strong>${it.productTitle}</strong><br/>
                    <span style="font-size: 11px; color: #475569;">SKU: ${it.skuCode} • Color: ${it.colorName} • Size: ${it.sizeLabel}</span>
                </td>
                <td style="padding: 8px; text-align: center;">${it.hsnCode}</td>
                <td style="padding: 8px; text-align: center; font-weight: 700;">${it.quantity}</td>
                <td style="padding: 8px; text-align: right;">₹${Number(it.taxableValue).toFixed(2)}</td>
                <td style="padding: 8px; text-align: right;">
                    ${isIntraTN
                        ? `CGST: ₹${Number(it.cgstAmount).toFixed(2)}<br/>SGST: ₹${Number(it.sgstAmount).toFixed(2)}`
                        : `IGST: ₹${Number(it.igstAmount).toFixed(2)}`}
                    <small style="display:block; color:#64748b;">(${it.gstPercentage}%)</small>
                </td>
                <td style="padding: 8px; text-align: right; font-weight: 800;">₹${Number(it.lineTotal).toFixed(2)}</td>
            </tr>
        `).join('');

        const freeGiftRowHtml = order.freeGiftEligible ? `
            <tr style="border-bottom: 1px solid #cbd5e1; background: #ecfdf5; color: #065f46;">
                <td style="padding: 8px;">🎁</td>
                <td style="padding: 8px;">
                    <strong>${giftLabel}</strong><br/>
                    <span style="font-size: 11px;">Kiruthis Showroom Mega Offer on Orders ≥ ₹2,000</span>
                </td>
                <td style="padding: 8px; text-align: center;">6109</td>
                <td style="padding: 8px; text-align: center; font-weight: 700;">1</td>
                <td style="padding: 8px; text-align: right;">₹0.00</td>
                <td style="padding: 8px; text-align: right;">FREE PROMO</td>
                <td style="padding: 8px; text-align: right; font-weight: 800;">₹0.00</td>
            </tr>
        ` : '';

        container.innerHTML = `
            <div class="no-print" style="background: #ecfdf5; border: 2px solid #10b981; border-radius: 12px; padding: 16px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                <div>
                    <span style="background: #059669; color: #fff; font-size: 11px; font-weight: 800; padding: 3px 8px; border-radius: 6px;">✓ SAVED IN MYSQL DATABASE</span>
                    <h2 style="font-size: 20px; font-weight: 800; color: #065f46; margin-top: 4px;">
                        Order #${order.orderNumber} Confirmed!
                    </h2>
                    <p style="font-size: 12.5px; color: #047857;">
                        Stock has been deducted in MySQL and your GST Tax Invoice #<strong>${order.invoiceNumber}</strong> is ready below.
                    </p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <button type="button" onclick="window.print()" class="btn-gold">🖨️ Print / Save GST Invoice PDF</button>
                    <a href="/" class="btn-outline-sm" style="text-decoration: none; display: inline-flex; align-items: center;">← Back to Storefront</a>
                </div>
            </div>

            <!-- OFFICIAL GST TAX INVOICE HEADER -->
            <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; border-bottom: 2px solid #0f172a; padding-bottom: 16px;">
                <div>
                    <div style="font-family: 'Cinzel', serif; font-size: 22px; font-weight: 800; color: #0f172a;">
                        KIRUTHIS MENS FASHIONS
                    </div>
                    <div style="font-family: 'Noto Sans Tamil', sans-serif; font-size: 12px; color: #1e3a8a; font-weight: 700;">
                        கிருத்திஸ் மென்ஸ் பேஷன்ஸ் — The Trendy Store (MENS | KIDS | WOMENS)
                    </div>
                    <p style="font-size: 12px; color: #334155; margin-top: 4px;">
                        Amman Nagar Bus Stop, Ganapathipalayam Road, Chennimalaipalayam, Tirupur - 641 605, Tamil Nadu<br/>
                        <strong>GSTIN:</strong> 33JODPK0345L1ZW (State Code: 33) • <strong>Phone/WhatsApp:</strong> +91 80988 73594 / +91 86104 26958
                    </p>
                </div>
                <div style="text-align: right; font-size: 12.5px;">
                    <span style="background: #0f172a; color: #facc15; font-weight: 800; padding: 4px 10px; border-radius: 6px; display: inline-block;">
                        TAX INVOICE
                    </span>
                    <p style="margin-top: 6px;"><strong>Invoice No:</strong> ${order.invoiceNumber}</p>
                    <p><strong>Order No:</strong> ${order.orderNumber}</p>
                    <p><strong>Payment Status:</strong> ${order.paymentMode} (${order.paymentStatus})</p>
                </div>
            </div>

            <!-- CUSTOMER & SHIPPING SNAPSHOT -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding: 14px 0; border-bottom: 1px solid #cbd5e1; font-size: 12.5px;">
                <div>
                    <strong style="color: #64748b; text-transform: uppercase; font-size: 11px;">Billed &amp; Shipped To:</strong>
                    <p style="font-size: 14px; font-weight: 800; margin-top: 2px;">${order.customerName}</p>
                    <p>${order.shippingAddressLine1}${order.shippingLandmark ? ', ' + order.shippingLandmark : ''}</p>
                    <p><strong>${order.shippingCity}, ${order.shippingState} - ${order.shippingPincode}</strong> (State Code: ${order.shippingStateCode})</p>
                    <p>Mobile: <strong>+91 ${order.customerPhone}</strong></p>
                </div>
                <div style="text-align: right;">
                    <strong style="color: #64748b; text-transform: uppercase; font-size: 11px;">Dispatch &amp; Tax Regime:</strong>
                    <p style="margin-top: 2px;"><strong>Tax Type:</strong> ${isIntraTN ? 'Intra-State Tamil Nadu (CGST + SGST)' : 'Inter-State (IGST)'}</p>
                    <p><strong>Parcel Weight:</strong> ${order.totalWeightGrams} grams</p>
                    <p><strong>Transaction Ref:</strong> ${order.gatewayPaymentId || 'COD-Verified'}</p>
                </div>
            </div>

            <!-- LINE ITEMS TABLE -->
            <table style="width: 100%; border-collapse: collapse; font-size: 12.5px; margin-top: 12px;">
                <thead>
                    <tr style="background: #f1f5f9; border-bottom: 2px solid #0f172a;">
                        <th style="padding: 8px; text-align: left;">#</th>
                        <th style="padding: 8px; text-align: left;">Garment Description &amp; SKU</th>
                        <th style="padding: 8px; text-align: center;">HSN</th>
                        <th style="padding: 8px; text-align: center;">Qty</th>
                        <th style="padding: 8px; text-align: right;">Taxable</th>
                        <th style="padding: 8px; text-align: right;">GST Split</th>
                        <th style="padding: 8px; text-align: right;">Total</th>
                    </tr>
                </thead>
                <tbody>
                    ${rowsHtml}
                    ${freeGiftRowHtml}
                </tbody>
            </table>

            <!-- TOTALS FOOTER -->
            <div style="display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 16px; margin-top: 18px; padding-top: 14px; border-top: 2px solid #0f172a; font-size: 13px;">
                <div style="font-size: 11.5px; color: #475569; max-width: 420px;">
                    <strong>Showroom Declaration:</strong> Certified that the particulars given above are true and correct. Eligible for 7-Day Doorstep Size Exchange with original brand tags intact.
                </div>
                <div style="min-width: 260px;">
                    <div class="summary-line"><span>Total Taxable Value:</span> <strong>₹${Number(order.totalTaxableAmount).toFixed(2)}</strong></div>
                    ${isIntraTN ? `
                    <div class="summary-line"><span>Total CGST:</span> <span>₹${Number(order.cgstAmount).toFixed(2)}</span></div>
                    <div class="summary-line"><span>Total SGST:</span> <span>₹${Number(order.sgstAmount).toFixed(2)}</span></div>` : `
                    <div class="summary-line"><span>Total IGST:</span> <span>₹${Number(order.igstAmount).toFixed(2)}</span></div>`}
                    ${Number(order.codHandlingFee) > 0 ? `
                    <div class="summary-line"><span>COD Handling Fee:</span> <span>₹${Number(order.codHandlingFee).toFixed(2)}</span></div>` : ''}
                    <div class="summary-line summary-total" style="margin-top: 6px;">
                        <span>Grand Total:</span>
                        <span>₹${Number(order.grandTotal).toLocaleString('en-IN')}</span>
                    </div>
                </div>
            </div>
        `;

        section.classList.remove('hidden');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // =========================================================================
    // 5. INITIALIZE CHECKOUT PAGE EVENTS
    // =========================================================================
    function initCheckoutEvents() {
        document.getElementById('chkState').addEventListener('change', renderOrderSummary);
        document.getElementById('chkPincode').addEventListener('input', renderOrderSummary);

        document.querySelectorAll('input[name="chkPaymentMode"]').forEach(radio => {
            radio.addEventListener('change', () => {
                const mode = getSelectedPaymentMode();
                document.getElementById('payCardOnline').classList.toggle('active', mode === 'ONLINE_PREPAID');
                document.getElementById('payCardCod').classList.toggle('active', mode === 'COD');
                document.getElementById('onlinePaymentSubBox').classList.toggle('hidden', mode !== 'ONLINE_PREPAID');
                document.getElementById('codOtpSubBox').classList.toggle('hidden', mode !== 'COD');
                renderOrderSummary();
            });
        });

        document.querySelectorAll('.upi-choice-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                selectedUpiApp = btn.getAttribute('data-upi');
                document.querySelectorAll('.upi-choice-btn').forEach(b => b.classList.toggle('active', b === btn));
            });
        });

        document.getElementById('sendCodOtpApiBtn').addEventListener('click', async () => {
            const phone = document.getElementById('chkPhone').value.trim();
            const fb = document.getElementById('codOtpFeedback');
            try {
                const resp = await fetch('/api/v1/orders/send-cod-otp', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ phone })
                });
                const data = await resp.json();
                if (resp.ok && data.demoOtp) {
                    document.getElementById('codOtpCodeInput').value = data.demoOtp;
                    fb.textContent = data.message;
                    fb.style.color = '#059669';
                } else {
                    fb.textContent = data.message || 'Check mobile number';
                    fb.style.color = '#dc2626';
                }
            } catch (e) {
                fb.textContent = 'Could not send OTP';
            }
        });

        document.getElementById('confirmPlaceOrderBtn').addEventListener('click', submitOrderToBackend);
    }

    document.addEventListener('DOMContentLoaded', () => {
        initCheckoutEvents();
        renderFreeGiftSelector();
        renderOrderSummary();
    });
})();