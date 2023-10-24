public IMoney addMoney(Money m) {
    return MoneyBag.create(m, this);
}