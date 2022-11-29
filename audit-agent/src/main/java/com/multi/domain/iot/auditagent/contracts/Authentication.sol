pragma solidity ^0.4.24;
pragma experimental ABIEncoderV2;

import './Table.sol';

contract Authentication {
    event addAuthenticationEvent(string, string, string, int256, int256);

    TableFactory tableFactory;
    string[] pidArray;

    string constant TABLE_NAME = "authentication_information_test2";

    constructor(){
        tableFactory = TableFactory(0x1001);
        tableFactory.createTable(TABLE_NAME, "pid", "identityProtectionInformation,host,port");
    }

    function getAllAuthenticationInformation()
    public returns (string[] memory, string[] memory, string[] memory, int256[] memory){
        Table table = tableFactory.openTable(TABLE_NAME);
        Condition condition = table.newCondition();
        uint256 total = pidArray.length;
        string[] memory item_pid_list = new string[](total);
        string[] memory item_identityProtectionInformation_list = new string[](total);
        string[] memory item_host_list = new string[](total);
        int256[] memory item_port_list = new int256[](total);

        for (uint256 i = 0; i < total; ++i) {
            (
            item_pid_list[i],
            item_identityProtectionInformation_list[i],
            item_host_list[i],
            item_port_list[i]
            ) = querySingleAuthenticationInformation(pidArray[i]);
        }
        return (item_pid_list,item_identityProtectionInformation_list,item_host_list,item_port_list);
    }


    function querySingleAuthenticationInformation(string memory _pid)
    public view
    returns (string memory, string memory, string host, int256 port)
    {
        Table table = tableFactory.openTable(TABLE_NAME);
        Condition condition = table.newCondition();

        Entries entries = table.select(_pid, condition);
        if(entries.size() == 0){
            return("","","",0);
        }
        return (
        entries.get(0).getString("pid"),
        entries.get(0).getString("identityProtectionInformation"),
        entries.get(0).getString("host"),
        entries.get(0).getInt("port")
        );
    }

    function addAuthenticationInformation(
        string memory _pid,
        string memory _identityProtectionInformation,
        string memory _host,
        int256 _port
    )
    public returns (int256){

        Table table = tableFactory.openTable(TABLE_NAME);
        Entry entry = table.newEntry();

        entry.set("pid", _pid);
        entry.set("identityProtectionInformation", _identityProtectionInformation);
        entry.set("host", _host);
        entry.set("port", _port);
        int256 count = table.insert(_pid, entry);
        pidArray.push(_pid);

        emit addAuthenticationEvent(_pid, _identityProtectionInformation, _host, _port, count);
        return count;
    }

    function isAuthorized(string memory _pid) public view returns (bool){
        Table table = tableFactory.openTable(TABLE_NAME);
        Condition condition = table.newCondition();
        Entries entries = table.select(_pid, condition);
        return entries.size() != 0;
    }

}