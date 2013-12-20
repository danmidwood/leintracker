/**
 * @jsx React.DOM
 */
//(function (window, React) {
  var Repositories = React.createClass({
    getInitialState: function() {
      $.ajax({
        url: 'repos/' + this.props.username,
        success: function(data) {
          this.setState({data: data});
        }.bind(this)
      });
      return {data: []};
    },
    render: function() {
      var repositoryNodes = this.state.data.map(function (data) {
         return <Repository data={data} />;
      });
      return (
        <div className="inner-page repos">
          <div className="row-fluid" id="row">
            {repositoryNodes}
          </div>
        </div>
      )
    }
  });

  var GithubLink = React.createClass({
    render: function() {
      return (
          <a className="github-link">{this.props.full_name}<i className="icon-github-sign"/></a>
      );
    }
  });

  var IsLein = React.createClass({
     getInitialState: function() {
       // TOOD Get from server
       return {lein_file: undefined};
    },
    render: function() {
      var cx = React.addons.classSet;
      var classes = cx({
        'lein': this.state.lein_file,
        'not-lein': !this.state.lein_file
      });
      return (
          <div className={classes}>Leiningen: {this.state.lein_file}</div>
      );
    }
  });


  var Tracked = React.createClass({
     getInitialState: function() {
       // TOOD Get from server
       return {selected: false};
    },
    toggle: function() {
      this.setState({selected : !this.state.selected});
      // TODO update on server
    },
    render: function() {
      var cx = React.addons.classSet;
      var classes = cx({
        'selected': this.state.selected,
        'not-selected': !this.state.selected
      });
      return (
          <div><a href="#" className={classes} onClick={this.toggle}>Tracked:<i className="icon-"/></a></div>
      );
    }
  });


  var Repository = React.createClass({
    render: function() {
      return (
        <div className="repository-selection-box span4">
          <div className="repository-selection-heading">
            <div className="repo-name">{this.props.data.name}</div>
            <div className="repo-description">{this.props.data.description}</div>
          </div>
          <div className="repository-selection-inner">
          <Tracked />
          <IsLein />
          <GithubLink full_name={this.props.data.full_name} />
          </div>
        </div>
      );
    }
  });


   React.renderComponent(
     <Repositories username="danmidwood" />,
     document.getElementById('row')
   );
