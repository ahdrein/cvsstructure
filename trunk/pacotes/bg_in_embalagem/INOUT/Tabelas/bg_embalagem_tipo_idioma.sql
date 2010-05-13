conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     TABELA: BG_EMBALAGEM_TIPO_IDIOMA
--  ///////

delete from DEPEND_HEADER_ITEM_TMP;

delete from COLUNAS_TAB_INTERFACE where TABLE_NAME = 'BG_EMBALAGEM_TIPO_IDIOMA';

begin

  update	TAB_INTERFACE             
  set		DESCRICAO = '',
		CTL_NAME = 'BG_EMBALAGEM_TIPO_IDIOMA', 
		PREFIX_FILE = 'EMBTID', 
		GERAR_CTL = 'S', 
		PRIORIDADE = 0, 
		ODBC_SOURCE_NAME = '', 
		ODBC_USER = '', 
		ODBC_PASSWORD = '', 
		ODBC_TABLE_NAME = '', 
		ODBC_WHERE = '', 
		ODBC_SELECT_ESPECIFICO = '', 
		TIPO_INTERFACE = 'LOADER',
		ORACLE_INITIAL_EXTENT = '', 
		ORACLE_NEXT_EXTENT = '', 
		ORACLE_INDEX_TABLESPACE = '',
		ELIMINAR_REG_EXECUCAO = 'N',
		ID_SISTEMA = 'BG',
		PROCEDURE_NAME = 'PKG_SFW_EMBALAGEM.PRC_EMBALAGEM_TIPO_IDIOMA',
		SEPARADOR = '',
		CTL_FIXO = null,
		TRIGGER1 = null,
		TRIGGER2 = null,
		COMANDO_EXTRA_LOADER = ''
  where TABLE_NAME = 'BG_EMBALAGEM_TIPO_IDIOMA';

  if SQL%notfound then
		insert into TAB_INTERFACE 
		(TABLE_NAME,
		DESCRICAO, 
		CTL_NAME, 
		PREFIX_FILE, 
		GERAR_CTL, 
		PRIORIDADE, 
		ODBC_SOURCE_NAME, 
		ODBC_USER, 
		ODBC_PASSWORD, 
		ODBC_TABLE_NAME, 
		ODBC_WHERE, 
		ODBC_SELECT_ESPECIFICO, 
		TIPO_INTERFACE, 
		ORACLE_INITIAL_EXTENT, 
		ORACLE_NEXT_EXTENT, 
		ORACLE_INDEX_TABLESPACE, 
		ELIMINAR_REG_EXECUCAO, 
		COMANDO_EXTRA_LOADER, 
		ID_SISTEMA, 
		PROCEDURE_NAME, 
		SEPARADOR)
		values
		('BG_EMBALAGEM_TIPO_IDIOMA',
		'',
		'BG_EMBALAGEM_TIPO_IDIOMA', 
		'EMBTID', 
		'S', 
		0, 
		'', 
		'', 
		'', 
		'', 
		'', 
		'', 
		'LOADER', 
		'', 
		'', 
		'',
		'N',
		'',
		'BG',
		'PKG_SFW_EMBALAGEM.PRC_EMBALAGEM_TIPO_IDIOMA',
		'');
  end if;
end;
/


insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_EMBALAGEM_TIPO_IDIOMA', 'TP_EMB', 'CHAR', '', '', '20', 
'1','Tipo de Embalagem','P_TP_EMB','');

insert into DEPEND_HEADER_ITEM
(	TABLE_NAME,
	COLUMN_NAME,
	TABLE_NAME_HEADER,
	COLUMN_NAME_HEADER, 
	OBRIG)
values	
(	'BG_EMBALAGEM_TIPO_IDIOMA' ,
	'TP_EMB' ,
	'BG_EMBALAGEM_TIPO' ,
	'TP_EMB',
	'N' );

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_EMBALAGEM_TIPO_IDIOMA', 'COD_IDIOMA', 'CHAR', '', '', '20', 
'2','Código do idioma','P_COD_IDIOMA','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_EMBALAGEM_TIPO_IDIOMA', 'DESC_TP_EMB', 'CHAR', '', '', '150', 
'3','Tradução do tipo da embalagem','P_TRADUCAO_TP_EMB','');

commit;

set serveroutput on
exec PRC_SINCRONIZA_TABELA('BG_EMBALAGEM_TIPO_IDIOMA');

CREATE OR REPLACE
TRIGGER TI_BG_EMBALAGEM_TIPO_IDIOMA
BEFORE INSERT ON BG_EMBALAGEM_TIPO_IDIOMA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	select seq_id_interface.nextval into :new.id from dual;

  begin
    select id
    into :new.id_ref
    from BG_EMBALAGEM_TIPO
    where id_importacao = :new.id_importacao
    and TP_EMB = :new.TP_EMB;
  exception
    when no_data_found then
      :new.id_ref := null;   
    when others then
      raise_application_error(-20001,'Falha em busca de header: ' || sqlerrm(sqlcode()));
  end;
 
end;
/


CREATE OR REPLACE
TRIGGER TIA_BG_EMBALAGEM_TIPO_IDIOMA
AFTER INSERT ON BG_EMBALAGEM_TIPO_IDIOMA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	insert into REGISTROS_INTERFACES (ID, ID_IMPORTACAO, TABLE_NAME, ID_REF)
	values (:new.id, :new.id_importacao, 'BG_EMBALAGEM_TIPO_IDIOMA', :new.id_ref);
end;
/


CREATE OR REPLACE
TRIGGER TD_BG_EMBALAGEM_TIPO_IDIOMA
BEFORE DELETE ON BG_EMBALAGEM_TIPO_IDIOMA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	delete from REGISTROS_INTERFACES where id = :old.id;
end;
/


 
-- //////
-- //////  Não esqueça de reprocessar todos os GRANTS para todos os sistemas
-- //////

