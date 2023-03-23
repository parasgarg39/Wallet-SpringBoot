package com.walletapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    private WalletJpaRepository walletRepository;

    @Override
    public WalletDto registerWallet(WalletDto wallet) throws WalletException {
//        return this.walletRepository.createWallet(wallet);
          return this.walletRepository.save(wallet);
    }

    @Override
    public WalletDto getWalletById(Integer walletId) throws WalletException {

//        WalletDto wallet = this.walletRepository.getWalletById(walletId);
        Optional<WalletDto> walletOptional = this.walletRepository.findById(walletId);
//        WalletDto wallet = this.walletRepository.getWalletById(walletId);
        if(walletId==null)
            throw new WalletException("Wallet id does not exists:"+walletId);
        return walletOptional.get();
    }

    @Override
    public WalletDto updateWallet(WalletDto wallet) throws WalletException {
        return this.walletRepository.save(wallet);
//        Optional<EmployeeDto> employeeOptional = this.employeeJpaRepository.findById(employee.getId());
//        return this.walletRepository.updateWallet(wallet);
    }

    @Override
    public WalletDto deleteWalletById(Integer walletId) throws WalletException {
//        return this.walletRepository.deleteWalletById(walletId);
        Optional<WalletDto> walletOptional = this.walletRepository.findById(walletId);
        if(walletOptional.isEmpty())
            throw new WalletException("Wallet could not be Deleted, not found id:"+walletId);
        WalletDto foundWallet = walletOptional.get();
        this.walletRepository.delete(foundWallet);
        return foundWallet;
    }


    @Override
    public Double addFundsToWalletById(Integer walletId, Double amount) throws WalletException {
        Optional<WalletDto> walletOptional = this.walletRepository.findById(walletId);
        if(walletOptional.isEmpty())
            throw new WalletException("Wallet does not exists to add funds, id:"+walletId);
        Double newBalance = this.walletRepository.findById(walletId).get().getBalance();
        WalletDto walletDto=this.walletRepository.getReferenceById(walletId);
        walletDto.setBalance(newBalance+amount);
        this.walletRepository.save(walletDto);
        return this.walletRepository.findById(walletId).get().getBalance();
    }

    @Override
    public Double withdrawFundsFromWalletById(Integer walletById, Double amount) throws WalletException {
        Optional<WalletDto> walletOptional = this.walletRepository.findById(walletById);
//        WalletDto wallet = this.walletRepository.getWalletById(walletById);
        if(walletOptional.isEmpty())
            throw new WalletException("Wallet does not exists to withdraw, try using valid account id");
        Double balance= this.walletRepository.findById(walletById).get().getBalance();
        if(balance<amount)
            throw new WalletException("Insufficient balance, current balance:"+balance);
        balance-=amount;
        WalletDto walletDto=this.walletRepository.getReferenceById(walletById);
        walletDto.setBalance(balance);
        this.walletRepository.save(walletDto);
        return this.walletRepository.findById(walletById).get().getBalance();
    }

    @Override
    public Boolean fundTransfer(Integer fromWalletId, Integer toWalletId, Double amount) throws WalletException {

        Optional<WalletDto> walletOptional = this.walletRepository.findById(fromWalletId);
//        WalletDto fromWallet = this.walletRepository.getWalletById(fromWalletId);
        if(walletOptional.isEmpty())
            throw new WalletException("From wallet does not exists, id:"+fromWalletId);
        Optional<WalletDto> toWallet = this.walletRepository.findById(toWalletId);
        if(toWallet.isEmpty())
            throw new WalletException("To wallet does not exists, id:"+toWalletId);
        Double fromBalance = this.walletRepository.findById(fromWalletId).get().getBalance();
        if(fromBalance<amount)
            throw new WalletException("Insufficient balance, current balance:"+fromBalance);
        WalletDto fromWalletDto=this.walletRepository.getReferenceById(fromWalletId);
        fromWalletDto.setBalance(fromBalance-amount);


        Double toBalance = this.walletRepository.findById(toWalletId).get().getBalance();;
        WalletDto toWalletDto=this.walletRepository.getReferenceById(toWalletId);
        toWalletDto.setBalance(toBalance+amount);

        this.walletRepository.save(fromWalletDto);
        this.walletRepository.save(toWalletDto);
        return true;
    }

    @Override
    public Collection<WalletDto> getAllWallets() {
        return this.walletRepository.findAll();
    }
}
