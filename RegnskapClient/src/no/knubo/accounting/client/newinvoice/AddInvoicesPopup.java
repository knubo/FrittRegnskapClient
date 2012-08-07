package no.knubo.accounting.client.newinvoice;

import java.math.BigDecimal;
import java.util.Date;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.invoice.InvoiceSplitType;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AddInvoicesPopup extends DialogBox implements ClickHandler {

	private NamedButton addButton;
	private NamedButton cancelButton;
	private final BigDecimal amount;
	private ListBoxWithErrorText box;
	private final RegisterInvoiceChooseInvoiceTypePage caller;
	private final boolean repeat;

	public AddInvoicesPopup(RegisterInvoiceChooseInvoiceTypePage caller,
			Elements elements, BigDecimal amount, boolean repeat) {

		this.caller = caller;
		this.amount = amount;
		this.repeat = repeat;
		setText(elements.first_month());

		// Fordeler beløp med hensyn til periode ut fra måned som inngis. For
		// fakturaer som har forfallsdfato tidligere enn dagens dato tas ut.

		box = new ListBoxWithErrorText("month");
		for (int i = 1; i <= 12; i++) {
			box.addItem(Util.monthString(elements, i), String.valueOf(i));
		}

		VerticalPanel vp = new VerticalPanel();
		vp.add(box);

		FlowPanel buttons = new FlowPanel();

		addButton = new NamedButton("add", elements.add());
		addButton.addClickHandler(this);
		buttons.add(addButton);

		cancelButton = new NamedButton("cancel", elements.cancel());
		cancelButton.addClickHandler(this);
		buttons.add(cancelButton);

		vp.add(buttons);

		setWidget(vp);
		setAutoHideEnabled(true);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == cancelButton) {
			hide();
		}
		if (event.getSource() == addButton) {
			add();
		}
	}

	private void add() {
		int month = Integer.parseInt(Util.getSelected(box));

		if (caller.currentMonth > month) {
			month = caller.currentMonth;
		}

		int day = Integer.parseInt(caller.invoiceDueDay.getText());

		InvoiceSplitType splitType = InvoiceSplitType
				.getSplitType(caller.splitType);

		BigDecimal addAmount = calcAmount(splitType, month);

		int pos = 0;
		while (month <= 12) {
			Date date = Util.date(day, month - 1, caller.currentYear - 1900);
			caller.addRow(pos++, new Object[] { date, formatAmount(addAmount) });

			switch (splitType) {
			case HALF_YEAR:
				month += 6;
				break;
			case MONTH:
				month++;
				break;
			case NONE:
				break;
			case QUARTER:
				month += 3;
				break;
			default:
				month++;
				break;
			}

		}
		hide();
	}

	private BigDecimal calcAmount(InvoiceSplitType splitType, int month) {
		int loopMonth = month;
		if (splitType == InvoiceSplitType.NONE || repeat) {
			return amount;
		}

		int count = 0;

		while (loopMonth <= 12) {
			switch (splitType) {
			case HALF_YEAR:
				loopMonth += 6;
				break;
			case MONTH:
				loopMonth++;
				break;
			case NONE:
				break;
			case QUARTER:
				loopMonth += 3;
				break;
			default:
				loopMonth++;
				break;
			}
			count++;
		}
		if (count < 1) {
			count = 1;
		}

		Util.log("Count:" + count);

		return amount.divide(new BigDecimal(count).setScale(2));
	}

	private String formatAmount(BigDecimal addAmount) {
		return addAmount.setScale(2).toPlainString();
	}

}
