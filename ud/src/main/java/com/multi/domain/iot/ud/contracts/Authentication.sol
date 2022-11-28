pragma solidity ^0.4.24;

import './Table.sol';

contract Authentication {
    event addAuthenticationEvent(string,string,int256 count);

    TableFactory tableFactory;
    
    string constant TABLE_NAME = "AuthenticationInformation";
    
    constructor(){
        tableFactory = TableFactory(0x1001);
        tableFactory.createTable(TABLE_NAME,"pid","identityProtectionInformation");
    }
    
    

    function addAuthenticationInformation(
        string memory _pid,
        string memory _identityProtectionInformation
        ) 
    public returns (int256){
        
        Table table = tableFactory.openTable(TABLE_NAME);
        Entry entry = table.newEntry();
        
        entry.set("pid",_pid);
        entry.set("identityProtectionInformation",_identityProtectionInformation);
        int256 count = table.insert(_pid,entry);
        
        emit addAuthenticationEvent(_pid,_identityProtectionInformation,count);
        return count;
    }
    

    function isAuthorized(string memory _pid) public view returns(bool){
        Table table = tableFactory.openTable(TABLE_NAME);
        Condition condition = table.newCondition();
        Entries entries = table.select(_pid,condition);
        return entries.size() != 0;
    }
        
}