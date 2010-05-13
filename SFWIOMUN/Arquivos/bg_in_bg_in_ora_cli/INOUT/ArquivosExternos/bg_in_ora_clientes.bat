@echo off
rem &1 = Id da Importacao
rem &2 = Usuario
rem &3 = Password
rem &4 = TNS
rem &5 = Quantidade de Registros a processar
rem &6 = Id do Evento (EVENT_TYPE_ID)
rem &7 = Reprocessa Erro (0 = (1); 1 = (1,4); 2 = (4)) 

sqlplus -s /nolog @INTERFACES\EBS_PROCESSA_INTERFACE_ENTRADA.SQL %1 %2 %3 %4 1000 38 1
sqlplus -s /nolog @INTERFACES\EBS_PROCESSA_INTERFACE_ENTRADA.SQL %1 %2 %3 %4 1000 39 1
sqlplus -s /nolog @INTERFACES\EBS_PROCESSA_INTERFACE_ENTRADA.SQL %1 %2 %3 %4 1000 40 1

sqlplus -s %2/%3@%4 @PROCESSA_INTERFACE.SQL %1
